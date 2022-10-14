import React, {useEffect, useState} from "react";

import {AddEmail} from "./AddEmail";
import {EventCreateRequest} from "../../models/event";
import {formatToUTC} from "../../utils/date.utils";

type CreateEventFormProps = {
    owner:string;
    onSubmit: (result: EventCreateRequest) => void;
}

export const CreateEventForm = ({onSubmit, owner}: CreateEventFormProps) => {
    const [summary, setSummary] = useState('');
    const [location, setLocation] = useState('');
    const [description, setDescription] = useState('');
    const [attendees, setAttendees] = useState<string[]>([]);
    const [start_date, setStartDate] = useState('');
    const [end_date, setEndDate] = useState('');
    const [duration, setDuration] = useState(0);

    const addEmail = (email:string) => {
        setAttendees((prevState)=>[...prevState, email])
    }


    return (
        <form>
            <label htmlFor='summary'>Event Summary</label>
            <input type='text' id='summary' placeholder='summary' value={summary} onChange={(e) => setSummary(e.target.value)}/>
            <label htmlFor='location'>Event Location</label>
            <input type='text' id='location' placeholder='location' value={location} onChange={(e) => setLocation(e.target.value)}/>
            <label htmlFor='description'>Description</label>
            <input type='text' id='description' placeholder='description' value={description} onChange={(e) => setDescription(e.target.value)}/>
            <label htmlFor='attendees'>Attendees</label>
            <AddEmail emails={attendees} addEmail={addEmail}></AddEmail>
            <label htmlFor='start_date'>Start Date</label>
            <input type='datetime-local' id='start_date' placeholder='start date' value={start_date} onChange={(e) => setStartDate(e.target.value)}/>
            <label htmlFor='end_date'>End Date</label>
            <input type='datetime-local' id='end_date' placeholder='end date' value={end_date} onChange={(e) => setEndDate(e.target.value)}/>
            <label htmlFor='duration'>Event Duration</label>
            <input type='number' id='duration' placeholder='duration' value={duration} onChange={(e) => setDuration(+e.target.value)}/>

            <button onClick={(e) => {
                e.preventDefault()
                onSubmit({
                summary,
                location,
                description,
                attendees,
                start_date:formatToUTC(start_date),
                end_date:formatToUTC(end_date),
                duration,
                owner
            })}}>submit</button>
        </form>
    );
}