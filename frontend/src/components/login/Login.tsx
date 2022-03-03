import { Button, Checkbox, Divider, Form, Input, message, Modal, } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import Paragraph from 'antd/lib/typography/Paragraph';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRequestError, login, requestPasswordReset, resetPassword } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import EmailFormInput from '../inputs/EmailFormInput';


const Login: React.FC = () => {

    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const [loading, setLoading] = useState(false);
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);


    const LoginForm = () => {

        const [form] = useForm();

        const onSubmit = (values: any) => {
            setLoading(true);
            login(values.email, values.password)
                .then(user => {
                    authContext.login(user);
                    message.success("Login erfolgreich", 2);
                    navigate(AppRoutes.Main.Path);
                }).catch(err => {
                    message.error("Login fehlgeschlagen");
                    setLoading(false);
                });
        };

        return (
            <Form
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 10 }}
                form={form}
                onFinish={onSubmit}>
                <EmailFormInput />
                <Form.Item
                    label="Passwort"
                    name="password"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input.Password />
                </Form.Item>
                <Form.Item>
                    <Checkbox />
                </Form.Item>
                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <a onClick={e => setShowForgotPasswordModal(true)}>
                        Passwort vergessen?
                    </a>
                </Form.Item>
                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <Button loading={loading} htmlType='submit' type='primary'>
                        Anmelden
                    </Button>
                    <Divider />
                    Oder <a onClick={e => navigate(AppRoutes.Main.Subroutes.Register, { replace: true })}>
                        registrieren.
                    </a>
                </Form.Item>
            </Form>
        )
    };

    const ForgotPasswordModal = () => {

        const [form] = useForm();

        const onFormSubmit = (values: any) => {
            console.log(values);
            requestPasswordReset(values.email)
                .then(res => {
                    message.success("E-Mail wurde zugesendet");
                    setShowForgotPasswordModal(false);
                }, err => {
                    const reqErr = getRequestError(err);
                    message.error(`${reqErr.reason}`);
                });
        }

        return (
            <Modal
                title="Passwort vergessen?"
                visible={showForgotPasswordModal}
                onCancel={e => setShowForgotPasswordModal(false)}
                footer={[
                    <Button type='primary' onClick={e => form.submit()}>
                        Passwort zurücksetzen
                    </Button>
                ]}
            >
                <Form
                    name="login"
                    form={form}
                    labelCol={{ span: 8 }}
                    wrapperCol={{ span: 14 }}
                    onFinish={onFormSubmit}>
                    <Paragraph>
                        Geben Sie ihre E-Mail Addresse an, um für Ihr bestehendes
                        Konto ein neues Passwort zu erhalten.
                    </Paragraph>
                    <EmailFormInput />
                </Form>
            </Modal>
        )
    };

    return (
        <>
            <Title level={1}>
                Anmeldung
            </Title>
            <LoginForm />
            {showForgotPasswordModal && <ForgotPasswordModal />}
        </>
    )
}

export default Login;