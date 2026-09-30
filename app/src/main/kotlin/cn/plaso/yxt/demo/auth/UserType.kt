package cn.plaso.yxt.demo.auth

/** OpenAPI `userType`：学生、教师、助教。 */
enum class UserType(val apiValue: String, val displayName: String) {
    Student("student", "学生"),
    Teacher("teacher", "教师"),
    Assistant("assistant", "助教");

    companion object {
        fun fromApi(value: String?): UserType {
            return entries.firstOrNull { it.apiValue == value } ?: Student
        }
    }
}
