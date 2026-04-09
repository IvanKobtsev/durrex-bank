package nekit.corporation.transaction_impl.model

interface TransactionInteractions {

    fun onAccountToChange(id: String)

    fun onAccountFromChoose(id: Int)

    fun onSumChange(sum: Double)

    fun onTransferClick()

    fun descriptionChange(text: String)
}