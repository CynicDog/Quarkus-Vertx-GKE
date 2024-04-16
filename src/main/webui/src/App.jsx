import './App.css';
import { useEffect, useState } from 'react';
import theaterJS from 'theaterjs';

function App() {
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetch('/greeting')
            .then(response => {
                if (response.ok) {
                    return response.text();
                }
                throw new Error('Network response was not ok.');
            })
            .then(text => {
                setMessage(text);
            })
            .catch(error => {
                console.error('Fetch error:', error);
                setMessage('Failed to fetch data');
            });
    }, []);

    useEffect(() => {
        if (message) {
            const theater = theaterJS();

            theater
                .addActor('Quarkus', { speed: 1, accuracy: 0.7 })
                .addActor('Me', { speed: 0.9, accuracy: 0.8 })
                .addScene('Quarkus:Toc toc.', 1000)
                .addScene('Me:What?', 500)
                .addScene(`Quarkus:${message}`, 200)
                .addScene('Me:Nooo...', -3, '!!! ', 150, 'No! ', 150)
                .addScene('Me:Yuk! That\'s impossible!', 100)
                .addScene('Quarkus:It is time!', 100)
                .addScene('Quarkus:With your training and this power,', 100)
                .addScene('Quarkus:You will create awesome web apps.', 100)
                .addScene('Quarkus:It is your destiny!', 200)
                .addScene('Quarkus:Meet Quarkus UI with NO hAssle!', 200)
                .addScene('Me:Neat!', 200)
                .play();

            return () => {
                theater.destroy();
            };
        }
    }, [message]);

    return (
        <div className="actors-container">
            <div className="actor">
                <div className="actor-prefix">- Quarkus: &nbsp;</div>
                <div id="Quarkus" className="actor-content"></div>
            </div>
            <div className="actor">
                <div className="actor-prefix">- Me: &nbsp;</div>
                <div id="Me" className="actor-content"></div>
            </div>
        </div>
    );
}

export default App;
