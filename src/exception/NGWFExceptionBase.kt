package exception

import java.lang.Exception

abstract class NGWFExceptionBase(errorMsg : String) : Exception(errorMsg) {
}
