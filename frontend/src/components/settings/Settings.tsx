import { Button, Divider, Form, Input, Modal, Select } from 'antd';
import Title from 'antd/lib/typography/Title';
import React, { useState } from 'react';
import { LockOutlined } from '@ant-design/icons';
import { Course } from '../../types/Course';
import { useNavigate } from 'react-router-dom';
import { AppRoutes } from '../../types/AppRoutes';

const ChangePassword: React.FC = () => {

    const onSubmit = (values: any) => {

    };

    return (
        <Form
            name="login"
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 10 }}
            onFinish={onSubmit}>
            <Form.Item
                label="E-Mail"
                name="email"
                rules={[{ required: true, message: 'Pflichtfeld' }]}>
                <Input type={'email'} />
            </Form.Item>
            <Form.Item
                label="Passwort"
                name="password"
                rules={[{ required: true, message: 'Pflichtfeld' }]}>
                <Input.Password />
            </Form.Item>
            <Form.Item
                label="Passwort (bestätigen)"
                name="password"
                rules={[{ required: true, message: 'Pflichtfeld' }]}>
                <Input.Password />
            </Form.Item>
            <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                <Button htmlType='submit' type='primary'>
                    Passwort ändern
                </Button>
            </Form.Item>
        </Form>
    );
}

const Settings: React.FC = () => {

    const mockCourses: Array<Course> = [
        {
            id: 1,
            description: "WWI 19 MA SE A",
            year: 2019,
            specialization: "Software Engineering",
            courseOfDegree: "Wirtschaftsinformatik",
            block: "A"
        },
        {
            id: 2,
            description: "WWI 19 MA SE B",
            year: 2019,
            specialization: "Software Engineering",
            courseOfDegree: "Wirtschaftsinformatik",
            block: "B"
        },
    ];

    const [isChangePasswordModalVisible, setIsChangePasswordModalVisible] = useState(false);

    const onChangePassword = () => {

    };

    const onChangePasswordCancel = () => {
        setIsChangePasswordModalVisible(false);
    };

    return (
        <>
            <Title level={1}>
                Einstellungen
            </Title>
            <Form
                name="login"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 10 }}>
                <Form.Item
                    label="E-Mail"
                    name="email"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input disabled style={{ color: 'black' }} type={'email'} />
                </Form.Item>
                <Form.Item
                    label="Passwort">
                    <Button type="default" onClick={e => setIsChangePasswordModalVisible(true)}>
                        <LockOutlined />Passwort ändern
                    </Button>
                </Form.Item>
                <Divider />
                <Form.Item
                    label="Vorname"
                    name="firstname"
                    rules={[{ required: false, message: 'Pflichtfeld' }]}>
                    <Input />
                </Form.Item>
                <Form.Item
                    label="Nachname"
                    name="lastname"
                    rules={[{ required: false, message: 'Pflichtfeld' }]}>
                    <Input />
                </Form.Item>
                <Form.Item
                    label="Kurs"
                    name="course"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Select>
                        {mockCourses.map(course => (
                            <Select.Option
                                key={`${course.id}`}
                                value={`${course.description}`}>
                                {course.description}
                            </Select.Option>
                        ))}
                    </Select>
                </Form.Item>

                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <Button htmlType='submit' type='primary'>
                        Aktualisieren
                    </Button>
                </Form.Item>
            </Form>

            <Modal
                title="Passwort ändern"
                visible={isChangePasswordModalVisible}
                onOk={onChangePassword}
                onCancel={onChangePasswordCancel}
                width={800}>
                <Form
                    name="login"
                    labelCol={{ span: 8 }}
                    wrapperCol={{ span: 14 }}>
                    <Form.Item
                        label="Neues Passwort"
                        name="password"
                        rules={[{ required: true, message: 'Pflichtfeld' }]}>
                        <Input.Password />
                    </Form.Item>
                    <Form.Item
                        label="Neues Passwort (bestätigen)"
                        name="password"
                        rules={[{ required: true, message: 'Pflichtfeld' }]}>
                        <Input.Password />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
}

export default Settings;