package android.leeseungyun.wearPhone

class CursorException(fileName: String, cursorName: String) :
    Exception("$fileName: Cursor($cursorName) - null")

class UnexpectedException(fileName: String, msg: String) : Exception("$fileName: $msg")
class PassException : Exception("PASS")