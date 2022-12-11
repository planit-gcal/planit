class API {
    static getPresets(): PresetDetails[] | "404" {
        let id = Authenticator.getId();
        const url = `${MAINURL}/plan-it/calendar/users/${id}/presets`
        const options = {
            'method': 'get' as const,
            'contentType': 'application/json',
            'muteHttpExceptions': true,
            'headers': {
                'Bypass-Tunnel-Reminder': '1',
            },
        }
        console.log(url)
        const response = UrlFetchApp.fetch(url, options);
        console.log(response.toString())
        if (response.getResponseCode() !== 200) return "404";
        return JSON.parse(response.toString());
    }

    static createEvent(createEventDTO: CreateEventDTO) {
        const url = `${MAINURL}/plan-it/calendar/events`
        const options = {
            'method': 'post' as const,
            'contentType': 'application/json',
            'headers': {
                'Bypass-Tunnel-Reminder': '1',
            },
            'payload': JSON.stringify(createEventDTO),
        }

        console.log(JSON.stringify(createEventDTO));
        const response = UrlFetchApp.fetch(url, options);
        if (response.getResponseCode() === 200) {
            return onHomepage();
        }
    }

    static getIdFromMail(email: string): LoginResponse {
        const url = `${MAINURL}/plan-it/oauth/users?email=${email}`
        console.log({url})
        const options = {
            'headers': {
                'method': 'get' as const,
                'contentType': 'application/json',
                'Bypass-Tunnel-Reminder': '1',
            },
        }
        try {
        const response = UrlFetchApp.fetch(url, options);
        console.log(response.toString());
        return JSON.parse(response.toString())
        }
        catch (e) {
            return null;
        }
    }
}
