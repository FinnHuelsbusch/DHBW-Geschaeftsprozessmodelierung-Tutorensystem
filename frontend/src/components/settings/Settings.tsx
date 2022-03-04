import { Button, Divider, Form, Input, Modal, Select, Tooltip } from 'antd';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useEffect, useState } from 'react';
import { LockOutlined } from '@ant-design/icons';
import { Course } from '../../types/Course';
import { AuthContext } from '../../context/UserContext';
import EmailFormInput from '../inputs/EmailFormInput';
import PasswordWithConfirm from '../register/PasswordWithConfirm';
import ChangePasswordModal from './PasswordChangeModal';
import { UserRole } from '../../types/User';

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

    const authContext = useContext(AuthContext);
    const [showChangePasswordModal, setShowChangePasswordModal] = useState(false);

    return (
        <>
            <Title level={1}>
                Einstellungen
            </Title>
            <Form
                name="login"
                labelCol={{ span: 8 }}
                initialValues={{ email: authContext.loggedUser?.email }}
                wrapperCol={{ span: 10 }}>
                <EmailFormInput disabled />
                <Form.Item
                    label="Passwort">
                    <Tooltip
                        title="Administratoren dürfen ihr Passwort nicht selbst ändern"
                        visible={authContext.hasRoles([UserRole.ROLE_ADMIN]) ? undefined : false}
                    >
                        <Button
                            type="default"
                            onClick={e => setShowChangePasswordModal(true)}
                            disabled={authContext.hasRoles([UserRole.ROLE_ADMIN])}>
                            <LockOutlined />Passwort ändern
                        </Button>
                    </Tooltip>
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

            <ChangePasswordModal
                visible={showChangePasswordModal}
                onClose={() => setShowChangePasswordModal(false)} />
        </>
    );
}

export default Settings;