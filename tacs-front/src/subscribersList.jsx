import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Table, Button } from 'react-bootstrap';
import axios from 'axios';
import { useTranslation } from 'react-i18next';

const SubscribersList = ({ onLogout }) => {
    const { articleId } = useParams();
    const { t } = useTranslation();
    const [subscribers, setSubscribers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchSubscribers();
    }, []);

    const fetchSubscribers = () => {
        axios.get(`${import.meta.env.VITE_API_URL}/articles/${articleId}/users`, {
            headers: {
                session: `${localStorage.getItem('authToken')}`
            }
        })
            .then(response => setSubscribers(response.data))
            .catch(async error => {
                if (error.response && error.response.status === 401) {
                    alert(t('loggedout'));
                    handleLogout();
                }
                else {
                    alert(`${t('subscribersList.errorFetchingSubscribers')} ${error}`);
                }
            });

                
    };
    const handleLogout = () => {
        onLogout();
        navigate('/'); // Redirect to home after logout
    };

    return (
        <div>
            <h2>{t('subscribersList.title')}</h2>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>{t('subscribersList.name')}</th>
                        <th>{t('subscribersList.surname')}</th>
                        <th>{t('subscribersList.email')}</th>
                        <th>{t('subscribersList.subscriptionDate')}</th>
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
            <Button onClick={() => window.history.back()}>{t('subscribersList.back')}</Button>
        </div>
    );
};

export default SubscribersList;