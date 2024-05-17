import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';

const ArticleForm = () => {
    const [formData, setFormData] = useState({
        name: '',
        image: '',
        deadline: '',
        usersMax: '',
        usersMin: '',
        cost: '',
        costType: '',
        userGets: '',
        owner: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        postFormData(formData);
    };

    return (
        <Form onSubmit={handleSubmit}>
            <Form.Group controlId="name">
                <Form.Label>Article Name</Form.Label>
                <Form.Control
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Enter article name"
                />
            </Form.Group>
            <Form.Group controlId="image">
                <Form.Label>Image URL</Form.Label>
                <Form.Control
                    type="text"
                    name="image"
                    value={formData.image}
                    onChange={handleChange}
                    placeholder="Enter image URL"
                />
            </Form.Group>
            <Form.Group controlId="deadline">
                <Form.Label>Deadline</Form.Label>
                <Form.Control
                    type="date"
                    name="deadline"
                    value={formData.deadline}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="usersMax">
                <Form.Label>Maximum Users</Form.Label>
                <Form.Control
                    type="number"
                    name="usersMax"
                    value={formData.usersMax}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="usersMin">
                <Form.Label>Minimum Users</Form.Label>
                <Form.Control
                    type="number"
                    name="usersMin"
                    value={formData.usersMin}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="cost">
                <Form.Label>Cost</Form.Label>
                <Form.Control
                    type="number"
                    name="cost"
                    value={formData.cost}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="costType">
                <Form.Label>Cost Type</Form.Label>
                <Form.Control
                    as="select"
                    name="costType"
                    value={formData.costType}
                    onChange={handleChange}
                >
                    <option value="1">Fixed</option>
                    <option value="2">Variable</option>
                </Form.Control>
            </Form.Group>
            <Form.Group controlId="userGets">
                <Form.Label>User Gets</Form.Label>
                <Form.Control
                    type="text"
                    name="userGets"
                    value={formData.userGets}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="owner">
                <Form.Label>Owner</Form.Label>
                <Form.Control
                    type="number"
                    name="owner"
                    value={formData.owner}
                    onChange={handleChange}
                />
            </Form.Group>
            <Button variant="primary" type="submit">
                Submit
            </Button>
        </Form>
    );
};

const postFormData = (formData) => {
    fetch('http://localhost:5283/articles', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then((response) => response.json())
        .then((data) => console.log(data))
        .catch((error) => console.error('Error:', error));
};

export default ArticleForm;