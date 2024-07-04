import React, { useEffect, useState } from 'react';
import { Table, Button, Container } from 'react-bootstrap';
import axios from 'axios';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

const SessionsList = ({ onLogout }) => {
    const { t } = useTranslation();
    const [sessions, setSessions] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchSessions();
    }, []);

    const fetchSessions = () => {
        axios.get(`${import.meta.env.VITE_API_URL}/users/session`, {
            headers: {
                session: `${localStorage.getItem('authToken')}`
            }
        })
            .then(response => setSessions(response.data))
            .catch(async error => {
                if (error.response && error.response.status === 401) {
                    alert(t('loggedout'));
                    handleLogout();
                }
                else {
                    alert(t('sessions.error'));
                }
            });


    };
    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('emailUser');
        onLogout();
        navigate('/'); // Redirect to home after logout
    };
    const handleCloseSession = (sessionId) => {
        axios.delete(`${import.meta.env.VITE_API_URL}/users/session`, {
            headers: {
                session: `${sessionId}`
            }
        })
            .then(response => {
                alert(t('sessions.success'));
                if (sessionId === localStorage.getItem('authToken')) {
                    handleLogout();
                } else {
                    setSessions(sessions.filter(session => session.sessionId !== sessionId));
                }
            })
            .catch(async error => {
                if (error.response && error.response.status === 401) {
                    alert(t('loggedout'));
                    handleLogout();
                } else {
                    alert(t('sessions.error'));
                }
            });
    };
    const handleCloseAllSessions = (sessionId) => {
        axios.delete(`${import.meta.env.VITE_API_URL}/users/sessions`, {
            headers: {
                session: `${localStorage.getItem('authToken')}`
            }
        })
            .then(response => {
                alert(t('sessions.success'));
                handleLogout();
            })
            .catch(async error => {
                if (error.response && error.response.status === 401) {
                    alert(t('loggedout'));
                    handleLogout();
                } else {
                    alert(t('sessions.error'));
                }
            });
    };

    return (
        <Container>
            <h1>{t('sessions.title')}</h1>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>{t('sessions.client')}</th>
                        <th>{t('sessions.name')}</th>
                        <th>{t('sessions.surname')}</th>
                        <th>{t('sessions.email')}</th>
                        <th>{t('sessions.actions')}</th>
                    </tr>
                </thead>
                <tbody>
                    {sessions.map(session => (
                        <tr key={session.sessionId}>
                            <td>{session.client}</td>
                            <td>{session.name}</td>
                            <td>{session.surname}</td>
                            <td>{session.email}</td>
                            <td>
                                <Button variant="danger" onClick={() => handleCloseSession(session.sessionId)}>
                                    {t('sessions.close')}
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <Button variant="danger" onClick={handleCloseAllSessions}>
                {t('sessions.closeAll')}
            </Button>
        </Container>
    );
};

export default SessionsList;