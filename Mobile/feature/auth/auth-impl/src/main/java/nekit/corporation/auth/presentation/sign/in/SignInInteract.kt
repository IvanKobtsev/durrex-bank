package nekit.corporation.auth.presentation.sign.`in`

interface SignInInteract {

    fun onLoginChange(login: String)

    fun onPasswordChange(password: String)

    fun onPasswordIconClick()

    fun onSignInClick()
}
