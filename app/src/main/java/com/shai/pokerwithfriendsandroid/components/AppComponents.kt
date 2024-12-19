package com.shai.pokerwithfriendsandroid.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.sharp.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shai.pokerwithfriendsandroid.R
import com.shai.pokerwithfriendsandroid.ui.theme.BorderColor
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.utils.PasswordValidator

@Composable
fun ImageComponent(image: Int) {
    Image(
        painter = painterResource(id = image),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .size(250.dp)
    )
}

@Composable
fun HeadingTextComponent(heading: String) {
    Text(
        text = heading,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 39.sp,
        color = Tertirary,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp
    )
}

enum class TextType {
    Text, Email, Password, Phone, Number
}

@Composable
fun MyTextField(
    labelVal: String,
    textType: TextType = TextType.Text,
    icon: Int? = null,
    vector: ImageVector? = null,
    fieldValue: String,
    onValueChange: (String) -> Unit,
    validation: Pair<Boolean, String> = Pair(true, ""),
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val typeOfKeyboard: KeyboardType = when (textType) {
        TextType.Email -> KeyboardType.Email
        TextType.Phone -> KeyboardType.Phone
        TextType.Number -> KeyboardType.Number
        else -> KeyboardType.Text
    }
    val (isValid, errorMsg) = validation
    OutlinedTextField(value = fieldValue,
        onValueChange = { newValue: String ->
            onValueChange(newValue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState -> onFocusChanged(focusState.hasFocus) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isValid) BrandColor else Color.Red,
            unfocusedBorderColor = if (isValid) BorderColor else Color.Red,
            focusedLeadingIconColor = if (isValid) BrandColor else Color.Red,
            unfocusedLeadingIconColor = if (isValid) Tertirary else Color.Red
        ),
        shape = MaterialTheme.shapes.small,
        placeholder = { Text(text = labelVal, color = Tertirary) },
        leadingIcon = {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon), contentDescription = "icon"
                )
            } else if (vector != null) {
                Icon(
                    imageVector = vector, contentDescription = "icon"
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = typeOfKeyboard, imeAction = ImeAction.Done
        ),
        singleLine = true
    )

    if (!isValid && errorMsg.isNotBlank()) {
        Text(
            text = errorMsg,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
fun PasswordInputComponent(
    placeholder: String,
    fieldValue: String,
    onValueChange: (String) -> Unit,
    validation: Pair<Boolean, String> = Pair(true, ""),
    onFocusChanged: (Boolean) -> Unit = {},
    isRegisterPassword: Boolean
) {
    var isShowPassword by remember {
        mutableStateOf(false)
    }
    var hasFocus by remember { mutableStateOf(false) }
    val (isValid, errorMsg) = validation
    OutlinedTextField(value = fieldValue,
        onValueChange = {
            onValueChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                hasFocus = focusState.hasFocus
                onFocusChanged(hasFocus)
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isValid) BrandColor else Color.Red,
            unfocusedBorderColor = if (isValid) BorderColor else Color.Red,
            focusedLeadingIconColor = if (isValid) BrandColor else Color.Red,
            unfocusedLeadingIconColor = if (isValid) Tertirary else Color.Red
        ),
        shape = MaterialTheme.shapes.small,
        placeholder = {
            Text(text = placeholder, color = Tertirary)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Sharp.Lock, contentDescription = "at_symbol", tint = Tertirary
            )
        },
        trailingIcon = {
            val description = if (isShowPassword) "Show Password" else "Hide Password"
            val iconImage =
                if (isShowPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            IconButton(onClick = {
                isShowPassword = !isShowPassword
            }) {
                Icon(
                    painter = painterResource(id = iconImage),
                    contentDescription = description,
                    tint = Tertirary,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation()
    )
    if (!isValid && errorMsg.isNotBlank()) {
        Text(
            text = errorMsg,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 5.dp)
        )
    } else if (isValid && isRegisterPassword && fieldValue.isNotEmpty() && !hasFocus) {
        val passwordStrength = PasswordValidator().passwordStrength(fieldValue)

        val (strengthLabel, strengthColor) = when (passwordStrength) {
            "Strong" -> "Strong Password" to BrandColor
            "Medium" -> "Medium Password" to Color.Yellow
            else -> "Weak Password" to Color.Red
        }

        Text(
            text = strengthLabel,
            color = strengthColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
fun EmailField(
    email: String,
    isValidEmail: Pair<Boolean, String>,
    isRegister: Boolean = false,
    validateEmail: () -> Unit = {},
    updateEmail: (String) -> Unit = {}
) {
    var hasInteracted by remember { mutableStateOf(false) }

    val onEmailFocusChanged: (Boolean) -> Unit = { hasFocus ->
        if (!hasFocus && hasInteracted) {
            validateEmail()
        }
    }

    MyTextField(
        labelVal = "E-mail",
        textType = TextType.Email,
        fieldValue = email,
        vector = Icons.Filled.Email,
        onValueChange = {
            if (isRegister) {
                hasInteracted = true
            }
            updateEmail(it)
        },
        validation = isValidEmail,
        onFocusChanged = onEmailFocusChanged
    )
}

@Composable
fun NameField(name: String, isValidName: Pair<Boolean, String>, onValueChange: (String) -> Unit) {
    MyTextField(
        labelVal = "Name",
        vector = Icons.Filled.Face,
        fieldValue = name,
        validation = isValidName,
        onValueChange = onValueChange
    )
}

@Composable
fun SearchTextField(
    searchTextFieldState: TextFieldState, onClearText: () -> Unit, labelVal: String = "Search"
) {
    val isFocused = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            state = searchTextFieldState,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    isFocused.value = focusState.hasFocus
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandColor,
                unfocusedBorderColor = BorderColor,
                focusedLeadingIconColor = BrandColor,
                unfocusedLeadingIconColor = Tertirary,
            ),
            shape = MaterialTheme.shapes.small,
            placeholder = { Text(text = labelVal, color = Tertirary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search icon",
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
        )
        AnimatedVisibility(visible = searchTextFieldState.text.isNotBlank()) {
            Icon(imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete icon",
                tint = Tertirary,
                modifier = Modifier.clickable {
                    onClearText()
                })
        }
    }
}

@Composable
fun PrimaryButton(
    labelVal: String,
    showProgress: Boolean = false,
    progressText: String = "",
    onPrimaryBtnClick: () -> Unit = {}
) {
    Button(
        onClick = { onPrimaryBtnClick() }, colors = ButtonDefaults.buttonColors(
            containerColor = BrandColor
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = if (!showProgress) labelVal else progressText,
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun SecondaryButton(
    labelVal: String,
    showProgress: Boolean = false,
    progressText: String = "",
    onSecondaryBtnClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = { onSecondaryBtnClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        border = BorderStroke(2.dp, BrandColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    color = BrandColor,
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = if (!showProgress) labelVal else progressText,
                color = BrandColor,
                fontSize = 18.sp
            )
        }
    }
}


@Composable
fun DividerWithText() {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), thickness = 1.dp, color = Tertirary
        )

        Text(
            text = "OR", modifier = Modifier.padding(10.dp), color = Tertirary, fontSize = 20.sp
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), thickness = 1.dp, color = Tertirary
        )
    }
}

/** Previews */

@Preview
@Composable
fun PreviewPrimaryButton() {
    Column {
        PrimaryButton("Continue")
        SecondaryButton("Secondary Button", showProgress = false)
    }
}