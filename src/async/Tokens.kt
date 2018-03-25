package async

abstract class Token {
    abstract fun getKeyName(): String

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true;
        } else {
            if (other is Token && this.getKeyName() == other.getKeyName()) {
                return true;
            }
        }
        return false;
    }
}

class DefaultToken(val name: String) : Token() {
    override fun getKeyName() = this.name
}

class NamespacedToken(val namespace: String, val name: String): Token() {
    override fun getKeyName() = "${this.namespace}_${this.name}"
}
