class Authenticator {

    static isAuthenticated(): boolean {
        const token = PropertyManager.getProperty<string>(userTokenString);
        return !!token && token !== "";
    }

    static getId(): string {
        return PropertyManager.getProperty<string>(userTokenString)
    }

    static setId(id: string) {
        PropertyManager.setProperty(userTokenString, id);
    }

    static authenticate(): boolean {
        const userEmail = Session.getActiveUser().getEmail();
        const response = API.getIdFromMail(userEmail);
        console.log({response})
        if (!response) {
            console.log("Error trying to authenticate")
            return false;
        }
        const id = response.planit_user_id;
        if (id === null) {
            console.log("id is null")
            return false;
        }
        console.log({id})
        Authenticator.setId(id);
        return true;
    }
}
