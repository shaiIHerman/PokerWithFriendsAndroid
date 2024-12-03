package com.shai.pokerwithfriendsandroid.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    labelVal: String,
    icon: Int? = null,
    vector: ImageVector? = null,
    fieldValue: String,
    onValueChange: (String) -> Unit,
    validation: Pair<Boolean, String> = Pair(true, ""),
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val typeOfKeyboard: KeyboardType = when (labelVal) {
        "email ID" -> KeyboardType.Email
        "mobile" -> KeyboardType.Phone
        else -> KeyboardType.Text
    }
    val (isValid, errorMsg) = validation
    OutlinedTextField(
        value = fieldValue,
        onValueChange = { newValue: String ->
            onValueChange(newValue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState -> onFocusChanged(focusState.hasFocus) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInputComponent(
    placeholder: String,
    fieldValue: String,
    onValueChange: (String) -> Unit,
    validation: Pair<Boolean, String> = Pair(true, ""),
) {
    var isShowPassword by remember {
        mutableStateOf(false)
    }
    val (isValid, errorMsg) = validation
    OutlinedTextField(
        value = fieldValue,
        onValueChange = {
            onValueChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
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
    }
}

@Composable
fun ForgotPasswordTextComponent(onClick: () -> Unit) {
    Text(
        text = "Forgot Password?",
        color = BrandColor,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun PrimaryButton(labelVal: String, onPrimaryBtnClick: () -> Unit = {}) {
    Button(
        onClick = { onPrimaryBtnClick() }, colors = ButtonDefaults.buttonColors(
            containerColor = BrandColor
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        Text(text = labelVal, color = Color.White, fontSize = 18.sp)
    }
}

@Composable
fun SecondaryButton(labelVal: String) {
    OutlinedButton(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(2.dp, BrandColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
    ) {
        Text(text = labelVal, color = BrandColor, fontSize = 18.sp)
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
        SecondaryButton("Secondary Button")
    }
}