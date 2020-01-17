package android.leeseungyun.wearPhone

// 커서 가져오기/ 검색 실패 예외
class CursorException(fileName: String, cursorName: String) :
    Exception("$fileName: Cursor($cursorName) - null")

// 다이얼 실패 예외
class DialException(fileName: String, phoneNumber: String) :
    Exception("$fileName: Call fail.($phoneNumber)")

// 예상치 못한 예외 처리 / 다른 파일에서 사용법 파악 실패로 인한 예외
class UnexpectedException(fileName: String, msg: String) : Exception("$fileName: $msg")

// 해당 파일 내에서 이미 예외 처리가 끝난 경우의 예외
class PassException : Exception("PASS")