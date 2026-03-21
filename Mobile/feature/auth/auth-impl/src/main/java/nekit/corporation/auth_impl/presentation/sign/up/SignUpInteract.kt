package nekit.corporation.auth_impl.presentation.sign.up

internal interface SignUpInteract {

    fun onLoginChange(login: String)

    fun onEmailChange(email: String)

    fun onFirstNameChange(firstName: String)

    fun onLastNameChange(lastName: String)

    fun onPhoneChange(phone: String)

    fun onPasswordChange(password: String)

    fun onPasswordIconClick()

    fun onRepeatPasswordChange(repeatPassword: String)

    fun onRepeatPasswordIconClick()

    fun onSignUpClick()
}
