const iconUrl = "https://cdn-icons-png.flaticon.com/512/2569/2569174.png"

function isError(errorEnum: error): boolean {
    const readErrors = GetProperty<error[]>(errorString);
    console.log("isError")
    console.log(readErrors)
    console.log((readErrors.includes(errorEnum)).toString())
    return readErrors.includes(errorEnum);
}

function setError(errorEnum: error, isActive: boolean) {
    const readErrors = GetProperty<error[]>(errorString);
    console.log(`setting error ${errorEnum} ${isActive}`);
    const isErrorAlready = readErrors.includes(errorEnum)
    if (isActive) {
        if (!isErrorAlready) {
            readErrors.push(errorEnum);
            SetProperty(errorString, readErrors);
        }
    } else {
        if (isErrorAlready) {
            const index = readErrors.indexOf(errorEnum);
            readErrors.splice(index, 1);
            SetProperty(errorString, readErrors);
        }
    }
}

function durationFormatError() {
    return errorText("Duration must be in 00:00 format")
}

function dateError() {
    return errorText("End date cannot be before start date")
}

function errorText(text: string) {
    return CardService.newDecoratedText()
        .setText(text)
        .setStartIcon(
            CardService.newIconImage()
                .setIcon(CardService.Icon.NONE)
                .setIconUrl(iconUrl)
                .setAltText('Error')
        )
        .setWrapText(false)

}