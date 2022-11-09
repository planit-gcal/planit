function getPresets() : PresetDetails[] | "404"
{
    let id = 1;
    const url = `${MAINURL}/plan-it/calendar/presets/${id}`
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
    return JSON.parse(response.toString());
}