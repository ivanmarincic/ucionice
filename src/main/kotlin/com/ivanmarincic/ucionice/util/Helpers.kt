package com.ivanmarincic.ucionice.util

import com.ivanmarincic.ucionice.model.User
import io.javalin.Javalin
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.experimental.xor


private fun hashPassword(password: String, salt: ByteArray, iterationCount: Int, keySize: Int): ByteArray {
    val spec = PBEKeySpec(password.toCharArray(), salt, iterationCount, keySize)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    return factory.generateSecret(spec).encoded
}

private fun fromHex(hex: String): ByteArray {
    val bytes = ByteArray(hex.length / 2)
    for (i in bytes.indices) {
        bytes[i] = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16).toByte()
    }
    return bytes
}

private fun toHex(array: ByteArray): String {
    val bi = BigInteger(1, array)
    val hex = bi.toString(16)
    val paddingLength = array.size * 2 - hex.length
    return if (paddingLength > 0) {
        String.format("%0" + paddingLength + "d", 0) + hex
    } else {
        hex
    }
}

fun hashPassword(password: String): String {
    val random = SecureRandom()
    val salt = ByteArray(16)
    val iterations = 65536
    random.nextBytes(salt)
    return "$iterations:${toHex(salt)}:${toHex(hashPassword(password, salt, iterations, 128 * 8))}"
}

fun validatePassword(password: String, hashedPassword: String): Boolean {
    val parts = hashedPassword.split(":")
    val iterations = parts[0].toInt()
    val salt = fromHex(parts[1])
    val hash = fromHex(parts[2])

    val testHash = hashPassword(password, salt, iterations, hash.size * 8)

    var diff = hash.size.xor(testHash.size)
    var i = 0
    while (i < hash.size && i < testHash.size) {
        diff = diff.or(hash[i].xor(testHash[i]).toInt())
        i++
    }
    return diff == 0
}

fun Javalin.routes(vararg groups: EndpointGroup): Javalin {
    groups.forEach {
        this.routes(it)
    }
    return this
}

fun Context.authenticatedUser(): User? {
    return this.sessionAttribute("user")
}


private const val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun randomString(len: Int): String {
    val random = SecureRandom()
    val sb = StringBuilder(len)
    for (i in 0 until len) sb.append(AB[random.nextInt(AB.length)])
    return sb.toString()
}

fun sendEmail() {

}
