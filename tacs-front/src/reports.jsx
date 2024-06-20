import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

const Reports = () => {
    const { t } = useTranslation();
    const [data, setData] = useState({
        totalUsers: null,
        totalArticles: null,
        successfulArticles: null,
        failedArticles: null,
        engagedUsers: null,
    });

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [totalUsers, totalArticles, successfulArticles, failedArticles, engagedUsers] = await Promise.all([
                    fetch(`${import.meta.env.VITE_API_URL}/reports/users`).then(res => res.json()),
                    fetch(`${import.meta.env.VITE_API_URL}/reports/articles`).then(res => res.json()),
                    fetch(`${import.meta.env.VITE_API_URL}/reports/articles/success`).then(res => res.json()),
                    fetch(`${import.meta.env.VITE_API_URL}/reports/articles/failed`).then(res => res.json()),
                    fetch(`${import.meta.env.VITE_API_URL}/reports/engaged_users`).then(res => res.json())
                ]);

                setData({
                    totalUsers,
                    totalArticles,
                    successfulArticles,
                    failedArticles,
                    engagedUsers,
                });
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return (
            <Container className="text-center my-5">
                <Spinner animation="border" />
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="text-center my-5">
                <p>{t('reports.loadingError')}{error.message}</p>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <Row>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>{t('reports.totalUsers')}</Card.Title>
                            <Card.Text>{data.totalUsers}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>{t('reports.totalArticles')}</Card.Title>
                            <Card.Text>{data.totalArticles}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>{t('reports.successfulArticles')}</Card.Title>
                            <Card.Text>{data.successfulArticles}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>{t('reports.failedArticles')}</Card.Title>
                            <Card.Text>{data.failedArticles}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>{t('reports.engagedUsers')}</Card.Title>
                            <Card.Text>{data.engagedUsers}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Reports;