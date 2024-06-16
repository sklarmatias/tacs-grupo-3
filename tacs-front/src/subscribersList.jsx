import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Table, Button } from 'react-bootstrap';
import axios from 'axios';

const SubscribersList = () => {
    const { articleId } = useParams();
    const [subscribers, setSubscribers] = useState([]);

    useEffect(() => {
        fetchSubscribers();
    }, []);

    const fetchSubscribers = () => {
        axios.get(`http://localhost:8080/restapp/articles/${articleId}/users`, {
            headers: {
                user: `${localStorage.getItem('authToken')}`
            }
        })
            .then(response => setSubscribers(response.data))
            .catch(error => alert('Error al obtener suscriptores:', error));
    };

    return (
        <div>
            <h2>Suscriptores</h2>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Email</th>
                        <th>Fecha</th>
                    </tr>
                </thead>
                <tbody>
                    {subscribers.map(subscriber => (
                        <tr key={subscriber.user.id}>
                            <td>{subscriber.user.name}</td>
                            <td>{subscriber.user.surname}</td>
                            <td>{subscriber.user.email}</td>
                            <td>{new Date(subscriber.created_at).toLocaleString()}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <Button onClick={() => window.history.back()}>Back</Button>
        </div>
    );
};

export default SubscribersList;