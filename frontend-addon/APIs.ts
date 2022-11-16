function getPresets() : PresetDetails[] | "404"
{
    let id = 1;
    const url = `${MAINURL}/plan-it/calendar/users/${id}/presets`
    const options = {
        'method' : 'get' as const,
        'contentType' : 'application/json',
        'muteHttpExceptions' : true,
        'headers' : {
            'Bypass-Tunnel-Reminder': '1',
        },
    }
    const response = UrlFetchApp.fetch(url, options);
    console.log(url)
    console.log(response.toString())
    if(response.getResponseCode() !== 200) return "404";
    return JSON.parse(response.toString());
}

function createEvent(createEventDTO: CreateEventDTO)
{
    const url = `${MAINURL}/plan-it/calendar/events`
    const options = {
        'method' : 'post' as const,
        'contentType' : 'application/json',
        'headers' : {
            'Bypass-Tunnel-Reminder': '1',
        },
        'body' : createEventDTO,
    }
    const response = UrlFetchApp.fetch(url, options);
    console.log(response);
    if(response.getResponseCode() === 200)
    {
        return onHomepage();
    }
}

