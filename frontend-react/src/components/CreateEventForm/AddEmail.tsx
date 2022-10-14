import React, {useEffect, useState} from "react";


type AddEmailProps = {
    emails: string[];
    addEmail: (email: string) => void;
}

export const AddEmail = ({emails, addEmail}: AddEmailProps) => {

    const [email, setEmail] = useState('');
    //TODO: add email validation
    return (
        <form>
            <div>{emails.map((e, i) => <div key={i}>{e}</div>)}</div>
            <input type='email' placeholder='email' value={email} onChange={(e) => setEmail(e.target.value)}/>
            <button onClick={(e)=> {e.preventDefault()
                addEmail(email)
                setEmail('');
            }}>+add email</button>
        </form>
    );
}