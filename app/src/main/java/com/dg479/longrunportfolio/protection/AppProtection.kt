package com.dg479.longrunportfolio.protection

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.security.MessageDigest
import java.security.KeyStore
import java.time.LocalDate
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

enum class ProtectionMode(
    val storageValue: String,
    val waitMillis: Long,
    val dailyEntryLimit: Int,
    val requiresPrinciple: Boolean,
    val requiresQrOnEntry: Boolean
) {
    NORMAL("normal", 0L, Int.MAX_VALUE, false, false),
    WEAK("weak", 10L * 60L * 1_000L, 24, false, false),
    MEDIUM("medium", 60L * 60L * 1_000L, 12, true, false),
    STRONG("strong", 4L * 60L * 60L * 1_000L, 2, false, true);

    companion object {
        fun fromStorage(value: String?): ProtectionMode = entries
            .firstOrNull { it.storageValue == value }
            ?: NORMAL
    }
}

data class ProtectionSettings(
    val mode: ProtectionMode = ProtectionMode.NORMAL,
    val qrHash: String = "",
    val qrContent: String = "",
    val investmentPrinciple: String = ""
) {
    val hasQr: Boolean get() = qrHash.isNotBlank()
}

data class ProtectionDailyUsage(
    val date: LocalDate,
    val entryCount: Int
)

object ProtectionStore {
    const val PreferencesName = "snowball_protection"
    const val UsageDateKey = "protection_usage_date"
    const val UsageCountKey = "protection_usage_count"
    private const val InstallMarkerFileName = "snowball_protection_install_marker"

    fun loadSettingsForAppLaunch(context: Context): ProtectionSettings {
        val settings = loadSettings(context)
        val installMarker = File(context.noBackupFilesDir, InstallMarkerFileName)
        if (installMarker.exists()) return settings

        val firstLaunchSettings = settings.copy(mode = ProtectionMode.NORMAL)
        saveSettings(context, firstLaunchSettings)
        runCatching {
            installMarker.parentFile?.mkdirs()
            installMarker.createNewFile()
        }
        return firstLaunchSettings
    }

    fun loadSettings(context: Context): ProtectionSettings {
        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        return ProtectionSettings(
            mode = ProtectionMode.fromStorage(preferences.getString("mode", null)),
            qrHash = preferences.getString("qr_hash", "").orEmpty(),
            qrContent = preferences.getString("qr_payload", null)
                ?.let(QrPayloadCipher::decrypt)
                .orEmpty(),
            investmentPrinciple = preferences.getString("investment_principle", "").orEmpty()
        )
    }

    fun saveSettings(context: Context, settings: ProtectionSettings) {
        val editor = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
            .edit()
            .putString("mode", settings.mode.storageValue)
            .putString("qr_hash", settings.qrHash)
            .putString("investment_principle", settings.investmentPrinciple.trim())
        when {
            settings.qrContent.isNotBlank() -> runCatching {
                editor.putString("qr_payload", QrPayloadCipher.encrypt(settings.qrContent))
            }
            settings.qrHash.isBlank() -> editor.remove("qr_payload")
        }
        editor.apply()
    }

    fun loadTodayUsage(context: Context, today: LocalDate = LocalDate.now()): ProtectionDailyUsage {
        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        val storedDate = runCatching {
            LocalDate.parse(preferences.getString(UsageDateKey, "").orEmpty())
        }.getOrNull()
        if (storedDate != today) {
            preferences.edit()
                .putString(UsageDateKey, today.toString())
                .putInt(UsageCountKey, 0)
                .apply()
            return ProtectionDailyUsage(today, 0)
        }
        return ProtectionDailyUsage(today, preferences.getInt(UsageCountKey, 0).coerceAtLeast(0))
    }

    fun recordEntry(context: Context, today: LocalDate = LocalDate.now()): ProtectionDailyUsage {
        val current = loadTodayUsage(context, today)
        val updated = current.copy(entryCount = current.entryCount + 1)
        context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
            .edit()
            .putString(UsageDateKey, today.toString())
            .putInt(UsageCountKey, updated.entryCount)
            .apply()
        return updated
    }
}

fun hashQrContent(content: String): String = MessageDigest.getInstance("SHA-256")
    .digest(content.toByteArray(Charsets.UTF_8))
    .joinToString("") { byte -> "%02x".format(byte) }

fun ProtectionSettings.matchesQr(content: String?): Boolean =
    !content.isNullOrEmpty() && hasQr && hashQrContent(content) == qrHash

private object QrPayloadCipher {
    private const val KeyAlias = "snowball_qr_payload_key"
    private const val Transformation = "AES/GCM/NoPadding"

    fun encrypt(content: String): String {
        val cipher = Cipher.getInstance(Transformation)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val encrypted = cipher.doFinal(content.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv, Base64.NO_WRAP) + "." +
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    fun decrypt(payload: String): String? = runCatching {
        val parts = payload.split('.', limit = 2)
        if (parts.size != 2) return@runCatching null
        val cipher = Cipher.getInstance(Transformation)
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
        cipher.doFinal(encrypted).toString(Charsets.UTF_8)
    }.getOrNull()

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KeyAlias, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").run {
            init(
                KeyGenParameterSpec.Builder(
                    KeyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            generateKey()
        }
    }
}
