class ErrorHandler {

    static UpdateError(errorEnum: error, newState: boolean): boolean {
        if (this.isError(errorEnum) === newState) {
            return false;
        } else {
            this.setError(errorEnum, newState)
            return true;
        }
    }

    static isError(errorEnum: error): boolean {
        const readErrors = PropertyManager.getProperty<error[]>(errorString);
        return readErrors.includes(errorEnum);
    }

    static setError(errorEnum: error, isActive: boolean) {
        const readErrors = PropertyManager.getProperty<error[]>(errorString);
        console.log(`setting error ${errorEnum} ${isActive}`);
        const isErrorAlready = readErrors.includes(errorEnum)
        if (isActive) {
            if (!isErrorAlready) {
                readErrors.push(errorEnum);
                PropertyManager.setProperty(errorString, readErrors);
            }
        } else {
            if (isErrorAlready) {
                const index = readErrors.indexOf(errorEnum);
                readErrors.splice(index, 1);
                PropertyManager.setProperty(errorString, readErrors);
            }
        }
    }

    static errorText(text: string) {
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
}
