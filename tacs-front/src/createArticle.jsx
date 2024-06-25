import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const ArticleForm = () => {
    const { t } = useTranslation();
    const [formData, setFormData] = useState({
        name: '',
        image: '',
        deadline: '',
        users_max: '',
        users_min: '',
        cost: '',
        cost_type: 'TOTAL',
        user_gets: '',
        link: ''
    });
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();

    const validateURL = (url) => {
        try {
            new URL(url);
            return true;
        } catch (_) {
            return false;
        }
    };

    const validateImageURL = (url) => {
        return (/\.(jpeg|jpg|gif|png)$/i).test(url);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const validateForm = () => {
        let formErrors = {};
        const now = new Date();
        const deadlineDate = new Date(formData.deadline);

        if (!formData.name) formErrors.name = t('form.validation.required');
        if (!formData.image) formErrors.image = t('form.validation.required');
        else if (!validateURL(formData.image) || !validateImageURL(formData.image)) {
            formErrors.image = t('form.validation.invalidImageURL');
        }
        if (!formData.deadline) formErrors.deadline = t('form.validation.required');
        else if (deadlineDate <= now) {
            formErrors.deadline = t('form.validation.futureDate');
        }
        if (!formData.users_max || formData.users_max <= 0) {
            formErrors.users_max = t('form.validation.greaterThanZero');
        }
        if (!formData.users_min || formData.users_min <= 0) {
            formErrors.users_min = t('form.validation.greaterThanZero');
        } else if (formData.users_max && formData.users_min >= formData.users_max) {
            formErrors.users_max = t('form.validation.greaterThanMin');
        }
        if (!formData.cost || formData.cost <= 0) {
            formErrors.cost = t('form.validation.greaterThanZero');
        }
        if (formData.link && !validateURL(formData.link)) {
            formErrors.link = t('form.validation.invalidURL');
        }
        if (!formData.user_gets) formErrors.user_gets = t('form.validation.required');

        return formErrors;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const formErrors = validateForm();
        if (Object.keys(formErrors).length === 0) {
            postFormData(formData);
        } else {
            setErrors(formErrors);
        }
    };

    const postFormData = (formData) => {
        fetch(`${import.meta.env.VITE_API_URL}/articles`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'user': `${localStorage.getItem('authToken')}`
            },
            body: JSON.stringify(formData)
        })
            .then((response) => {
                if (response.status === 201) {
                    alert(t('form.success'));
                    navigate('/myarticles');
                } else {
                    alert(t('form.error'), response.body);
                    console.error(response.body);
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                alert(t('form.error'));
            });
    };

    return (
        <Form onSubmit={handleSubmit}>
            <Form.Group controlId="name">
                <Form.Label>{t('form.articleName')}</Form.Label>
                <Form.Control
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder={t('form.enterArticleName')}
                    isInvalid={!!errors.name}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.name}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="image">
                <Form.Label>{t('form.imageURL')}</Form.Label>
                <Form.Control
                    type="text"
                    name="image"
                    value={formData.image}
                    onChange={handleChange}
                    placeholder={t('form.enterImageURL')}
                    isInvalid={!!errors.image}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.image}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="link">
                <Form.Label>{t('form.link')}</Form.Label>
                <Form.Control
                    type="text"
                    name="link"
                    value={formData.link}
                    onChange={handleChange}
                    placeholder={t('form.enterLink')}
                    isInvalid={!!errors.link}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.link}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="deadline">
                <Form.Label>{t('form.deadline')}</Form.Label>
                <Form.Control
                    type="date"
                    name="deadline"
                    value={formData.deadline}
                    onChange={handleChange}
                    isInvalid={!!errors.deadline}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.deadline}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="users_max">
                <Form.Label>{t('form.maximumUsers')}</Form.Label>
                <Form.Control
                    type="number"
                    name="users_max"
                    value={formData.users_max}
                    onChange={handleChange}
                    isInvalid={!!errors.users_max}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.users_max}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="users_min">
                <Form.Label>{t('form.minimumUsers')}</Form.Label>
                <Form.Control
                    type="number"
                    name="users_min"
                    value={formData.users_min}
                    onChange={handleChange}
                    isInvalid={!!errors.users_min}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.users_min}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="cost">
                <Form.Label>{t('form.cost')}</Form.Label>
                <Form.Control
                    type="number"
                    name="cost"
                    value={formData.cost}
                    onChange={handleChange}
                    isInvalid={!!errors.cost}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.cost}
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group controlId="cost_type">
                <Form.Label>{t('form.costType')}</Form.Label>
                <Form.Control
                    as="select"
                    name="cost_type"
                    value={formData.cost_type}
                    onChange={handleChange}
                >
                    <option value="TOTAL">{t('form.total')}</option>
                    <option value="PER_USER">{t('form.perUser')}</option>
                </Form.Control>
            </Form.Group>
            <Form.Group controlId="user_gets">
                <Form.Label>{t('form.userGets')}</Form.Label>
                <Form.Control
                    type="text"
                    name="user_gets"
                    value={formData.user_gets}
                    onChange={handleChange}
                    isInvalid={!!errors.user_gets}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.user_gets}
                </Form.Control.Feedback>
            </Form.Group>
            <Button variant="primary" type="submit">
                {t('form.submit')}
            </Button>
        </Form>
    );
};


export default ArticleForm;