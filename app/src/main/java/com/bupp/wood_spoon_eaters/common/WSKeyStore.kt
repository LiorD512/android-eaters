package com.bupp.wood_spoon_eaters.common

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class WSKeyStore {

    companion object{
        const val KEY_PROVIDER = "AndroidKeyStore"
        const val KEY_ALIAS = "myKeyAlias"
        const val KEY_TRANSFORMATION = "AES/CBC/NoPadding"
    }

    private fun keyStore() {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_PROVIDER)
        val keyParameterSpec = KeyGenParameterSpec.Builder(KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(keyParameterSpec)
        keyGenerator.generateKey()

//        val pair = encryptData("Test this encryption")
//
//        val decryptedData = decryptData(pair.first, pair.second)
//
//        val encrypted: String = pair.second.toString(Charsets.UTF_8)
//        Log.d("wowKeyStore","encryptedData: $encrypted")
//        Log.d("wowKeyStore","decryptedData: $decryptedData")
    }

    fun getKey(): SecretKey {
        val keyStore: KeyStore = KeyStore.getInstance(KEY_PROVIDER)
        keyStore.load(null)

        val secretKeyEntry: KeyStore.SecretKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey
    }

    fun encryptData(data: String): Pair<ByteArray, ByteArray>{
        val cipher = Cipher.getInstance(KEY_TRANSFORMATION)

        var temp: String = data
        while(temp.toByteArray().size % 16 != 0)
            temp += "\u0020"

        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val ivBytes = cipher.iv
        val encryptBytes: ByteArray = cipher.doFinal(temp.toByteArray(Charsets.UTF_8))
        return Pair(ivBytes, encryptBytes)
    }

    fun decryptData(ivBytes: ByteArray, data: ByteArray): String{
        val cipher = Cipher.getInstance(KEY_TRANSFORMATION)
        val spec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return cipher.doFinal(data).toString(Charsets.UTF_8).trim()
    }

}