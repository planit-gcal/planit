const settings: Settings = {
    breakIntoSmallerEvents: false,
    durationInMinutes: 0,
    endDate: undefined,
    maxLengthOfSingleEventInMinutes: 0,
    maxTimeBetweenEventsInMinutes: 0,
    minLengthOfSingleEventInMinutes: 0,
    minTimeBetweenEventsInMinutes: 0,
    numberOfEvents: 0,
    startDate: undefined

}

const jakub: Guest = {
        email: "JakubSeredyński@email.com",
        isRequired: true
    }

const lukasz: Guest = {
        email: "ŁukaszBlachnicki@email.com",
        isRequired: false
    }

const marcin: Guest = {
        email: "MarcinKasperski@email.com",
        isRequired: true,
    }

const mustafa: Guest = {
    email: "MustafaAlhamoud@email.com",
    isRequired: false,
}

const mom : Guest = {
    email : "YourMom@email.com",
    isRequired : true,
}

const dad : Guest = {
    email : "JustGoingForMilk@email.com",
    isRequired : false,
}

// function getPresets(): Preset[] {
//     return [
//         {
//             name: "project",
//             settings: settings,
//             guests : [jakub, lukasz, mustafa, marcin]
//         },
//         {
//             name: "family",
//             settings : settings,
//             guests: [mom, dad]
//         },
//         {
//             name : "random",
//             settings : settings,
//             guests : [lukasz, mom, jakub, dad]
//         }
//     ]
// }

function getPresets() : Preset[]
{
    let id = 1;
    const url = `${MAINURL}/plan-it/calendar/presets/${id}`
    const options = {
        'method' : 'get',
        'contentType' : 'application/json',
        'muteHttpExceptions' : true,
        'headers' : {
            'Bypass-Tunnel-Reminder': '1',
        },
    }
    // @ts-ignore
    const response = UrlFetchApp.fetch(url, options);
    console.log(url)
    console.log(response.toString())
    return JSON.parse(response.toString());
}