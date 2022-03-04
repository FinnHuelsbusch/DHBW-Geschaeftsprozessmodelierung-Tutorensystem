import { Button, Divider, Form, Input, message, Result, } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import { ValidateStatus } from 'antd/lib/form/FormItem';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRequestError, register } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { getErrorMessageString } from '../../types/RequestError';
import EmailFormInput from '../inputs/EmailFormInput';

const Register: React.FC = () => {

    const navigate = useNavigate();
    const [form] = useForm();
    const [loading, setLoading] = useState(false);
    const [showRegisterMessage, setShowRegisterMessage] = useState(false);

    const passwordsMatch = (password: string, passwordConfirm: string): boolean => {
        return (
            password != null && password.trim() !== ""
            && passwordConfirm != null && passwordConfirm === password
        );
    };

    const onSubmit = (values: any) => {
        if (!passwordsMatch(values.password, values.passwordConfirm)) {
            setPasswordFieldsInfo({
                validateStatus: 'error',
                message: 'Passwörter stimmen nicht überein'
            });
        } else {
            setPasswordFieldsInfo({
                validateStatus: 'success',
                message: undefined
            });
        }
        setLoading(true);
        register(values.email, values.password)
            .then(res => {
                setLoading(false);
                setShowRegisterMessage(true);
            }, err => {
                const reqErr = getRequestError(err);
                message.error(getErrorMessageString(reqErr.errorCode));
                setLoading(false);
            });
    };

    const validatePassword = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback("Pflichtfeld");
        } else if (!passwordsMatch(value, form.getFieldValue("passwordConfirm"))) {
            callback("Passwörter stimmen nicht überein");
        } else {
            callback();
        }
    }

    const validatePasswordConfirm = (rule: any, value: string, callback: any) => {
        if (!value || value === "") {
            callback("Pflichtfeld");
        } else if (!passwordsMatch(value, form.getFieldValue("password"))) {
            callback("Passwörter stimmen nicht überein");
        } else {
            callback();
        }
    }

    const [passwordFieldsInfo, setPasswordFieldsInfo] = useState<{
        validateStatus: ValidateStatus | undefined,
        message: string | undefined
    }>({
        validateStatus: undefined,
        message: undefined
    });

    const UserPasswordForm = () => (
        <Form
            name="login"
            form={form}
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 10 }}
            onFinish={onSubmit}>
            <EmailFormInput required disabled={loading} />
            <Form.Item
                label="Passwort"
                name="password"
                validateStatus={passwordFieldsInfo.validateStatus}
                help={passwordFieldsInfo.message}
                rules={[{ required: true }]}>
                <Input.Password
                    disabled={loading}
                />
            </Form.Item>

            <Form.Item
                label="Passwort wiederholen"
                name="passwordConfirm"
                validateStatus={passwordFieldsInfo.validateStatus}
                help={passwordFieldsInfo.message}
                rules={[{ required: true }]}>
                <Input.Password
                    disabled={loading}
                />
            </Form.Item>
            <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                <Button loading={loading} htmlType='submit' type='primary'>
                    Registrieren
                </Button>
                <Divider />
                Oder <a onClick={e => navigate(AppRoutes.Main.Subroutes.Login, { replace: true })}>
                    anmelden.
                </a>
            </Form.Item>
        </Form >
    );

    const RegisterMessage = () => (
        <Result
            status="success"
            title="Registrierung erfolgreich"
            extra={
                <Paragraph>
                    <Text style={{ fontSize: 16 }}>
                        An die angegebene E-Mail Adresse wurde eine Nachricht
                        mit einem Bestätigungslink gesendet.
                    </Text>
                    <br />
                    <Text style={{ fontSize: 16 }}>
                        Über den enthaltenen Link können Sie ein Konto erstellen.
                    </Text>
                </Paragraph>
            }>
        </Result>
    );

    return (
        <>
            <Title level={1}>
                Registrierung
            </Title>
            {showRegisterMessage ? <RegisterMessage /> : <UserPasswordForm />}
        </>
    )
}

export default Register;