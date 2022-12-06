class PlanitErrors
{
    static emailError() {
        return ErrorHandler.errorText("This is not valid email format")
    }

    static durationFormatError() {
        return ErrorHandler.errorText("Duration must be in 00:00 format")
    }

    static dateError() {
        return ErrorHandler.errorText("End date cannot be before start date")
    }
}