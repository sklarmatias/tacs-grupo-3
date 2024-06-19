import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner } from 'react-bootstrap';

const Reports = () => {
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
                    fetch(`http://localhost:8080/restapp/reports/users`).then(res => res.json()),
                    fetch(`http://localhost:8080/restapp/reports/articles`).then(res => res.json()),
                    fetch(`http://localhost:8080/restapp/reports/articles/success`).then(res => res.json()),
                    fetch(`http://localhost:8080/restapp/reports/articles/failed`).then(res => res.json()),
                    fetch(`http://localhost:8080/restapp/reports/engaged_users`).then(res => res.json())
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
                <p>Error loading data: {error.message}</p>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <Row>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Total Users</Card.Title>
                            <Card.Text>{data.totalUsers}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Total Articles</Card.Title>
                            <Card.Text>{data.totalArticles}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Successful Articles</Card.Title>
                            <Card.Text>{data.successfulArticles}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Failed Articles</Card.Title>
                            <Card.Text>{data.failedArticles}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title>Engaged Users</Card.Title>
                            <Card.Text>{data.engagedUsers}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Reports;