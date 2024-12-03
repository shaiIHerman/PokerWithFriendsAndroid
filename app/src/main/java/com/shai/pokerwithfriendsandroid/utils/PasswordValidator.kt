package com.shai.pokerwithfriendsandroid.utils

import java.util.Locale

class PasswordValidator {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MAX_PASSWORD_LENGTH = 20
        private val COMMON_PASSWORDS = listOf(
            "password", "123456", "qwerty", "abc123", "letmein", "monkey", "welcome"
        )

        // Regex to check password strength (min 8 characters, one uppercase, one lowercase, one digit, one special character)
        private val PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/\\?]).{8,20}\$".toRegex()
    }

    /**
     * Validate the password against multiple rules.
     *
     * @param password The password to validate.
     * @param confirmPassword The password confirmation (if applicable).
     * @return Pair of a Boolean indicating whether the password is valid and a message describing the result.
     */
    fun validate(password: String, confirmPassword: String? = null): Pair<Boolean, String> {
        // Rule 1: Check length constraints
        if (password.length < MIN_PASSWORD_LENGTH) {
            return Pair(false, "Password must be at least $MIN_PASSWORD_LENGTH characters.")
        }

        if (password.length > MAX_PASSWORD_LENGTH) {
            return Pair(false, "Password must be no more than $MAX_PASSWORD_LENGTH characters.")
        }

        // Rule 2: Check character variety (uppercase, lowercase, digits, special characters)
        if (!password.any { it.isUpperCase() }) {
            return Pair(false, "Password must contain at least one uppercase letter.")
        }

        if (!password.any { it.isLowerCase() }) {
            return Pair(false, "Password must contain at least one lowercase letter.")
        }

        if (!password.any { it.isDigit() }) {
            return Pair(false, "Password must contain at least one number.")
        }

        if (!password.any { it in "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/`~" }) {
            return Pair(false, "Password must contain at least one special character.")
        }

        // Rule 3: Check for common passwords
        if (isPasswordTooCommon(password)) {
            return Pair(false, "This password is too common. Please choose another.")
        }

        // Rule 4: If there is a confirmation password, check if it matches
//        if (confirmPassword != null && password != confirmPassword) {
//            return Pair(false, "Passwords do not match.")
//        }

        // Rule 5: Check against regex for a more complex validation
        if (!PASSWORD_REGEX.matches(password)) {
            return Pair(false, "Password does not meet the required strength criteria.")
        }

        // If all checks pass, the password is valid
        return Pair(true, "Password is valid.")
    }

    /**
     * Check if the password is too common by comparing it against a list of known common passwords.
     *
     * @param password The password to check.
     * @return True if the password is common, false otherwise.
     */
    private fun isPasswordTooCommon(password: String): Boolean {
        return COMMON_PASSWORDS.contains(password.lowercase(Locale.getDefault()))
    }

    /**
     * A utility function to check the password strength.
     *
     * @param password The password to check.
     * @return A string indicating the strength of the password ("Weak", "Medium", "Strong").
     */
    fun passwordStrength(password: String): String {
        val lengthScore = if (password.length >= 12) 2 else if (password.length >= 8) 1 else 0
        val varietyScore =
            if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() } && password.any { it.isDigit() } && password.any { it in "!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/\\?" }) 2 else 0
        val commonScore = if (isPasswordTooCommon(password)) 0 else 1

        val totalScore = lengthScore + varietyScore + commonScore

        return when (totalScore) {
            4 -> "Strong"
            3 -> "Medium"
            else -> "Weak"
        }
    }

    fun checkConfirmPassword(password: String?, confirmPassword: String?): Pair<Boolean, String> {
        return if (password == confirmPassword) {
            Pair(true, "")
        } else {
            Pair(false, "Passwords don't match")
        }
    }
}
