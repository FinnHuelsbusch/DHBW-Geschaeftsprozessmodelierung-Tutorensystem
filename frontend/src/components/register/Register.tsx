import { Button, Divider, Form, Input, message, Result, Select, } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import { ValidateStatus } from 'antd/lib/form/FormItem';
import Paragraph from 'antd/lib/typography/Paragraph';
import Text from 'antd/lib/typography/Text';
import Title from 'antd/lib/typography/Title';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCoursesWithTitleAndSpecialisations, getRequestError, register } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { CourseWithTitleAndSpecialisations } from '../../types/Course';
import { getErrorMessageString } from '../../types/RequestError';
import EmailFormInput, { isDirectorEmail } from '../inputs/EmailFormInput';
import PasswordWithConfirm, { PasswordFieldProps } from './PasswordWithConfirm';

const Register: React.FC = () => {

    const navigate = useNavigate();
    const [form] = useForm();

    const [courses, setCourses] = useState<CourseWithTitleAndSpecialisations[]>([]);
    const [loading, setLoading] = useState(false);
    const [isDirectorRegistration, setIsDirectorRegistration] = useState(false);
    const [showRegisterMessage, setShowRegisterMessage] = useState(false);

    const [passwordFieldsInfo, setPasswordFieldsInfo] = useState<{
        validateStatus: PasswordFieldProps['validateStatus'],
        message: PasswordFieldProps['message']
    }>({
        validateStatus: undefined,
        message: undefined
    });

    useEffect(() => {
        getCoursesWithTitleAndSpecialisations().then(courses => {
            setCourses(courses);
        }, err => {
            message.error(getErrorMessageString(getRequestError(err).errorCode))
        });
    }, []);

    const onSubmit = (values: any) => {
        if (!PasswordWithConfirm.passwordsMatch(values.password, values.passwordConfirm)) {
            setPasswordFieldsInfo({
                validateStatus: 'error',
                message: 'Passwörter stimmen nicht überein'
            });
        } else {
            setPasswordFieldsInfo({
                validateStatus: 'success',
                message: undefined
            });
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
        }
    };

    const onEmailInputChange = (value: string) => {
        if (isDirectorEmail(value)) {
            setIsDirectorRegistration(true);
        } else {
            setIsDirectorRegistration(false);
        }
    };

    const CourseOptionsList = (courses: CourseWithTitleAndSpecialisations[]) => {

        const options = courses.map(course =>
            <Select.OptGroup label={`${course.title} (${course.abbreviation})`}>
                {course.specialisationCourses.map(
                    specialisation => (
                        <Select.Option
                            key={`${course.id}`}
                            value={`${course.abbreviation} ${specialisation.abbreviation} ${course.title} ${specialisation.title}`}>
                            {specialisation.title} <i style={{ color: 'gray' }}>({course.abbreviation} {specialisation.abbreviation})</i>
                        </Select.Option>)
                )}
            </Select.OptGroup>);

        return options;
    };

    const UserPasswordForm = () => (
        <Form
            name="login"
            form={form}
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 10 }}
            onFinish={onSubmit}>
            <EmailFormInput
                required
                disabled={loading}
                onChange={onEmailInputChange} />

            <PasswordWithConfirm.Password
                disabled={loading}
                validateStatus={passwordFieldsInfo.validateStatus}
                message={passwordFieldsInfo.message}
            />

            <PasswordWithConfirm.PasswordConfirm
                disabled={loading}
                validateStatus={passwordFieldsInfo.validateStatus}
                message={passwordFieldsInfo.message}
            />

            <Divider />

            <Form.Item
                label="Vorname"
                name="firstname"
                rules={[{ required: true }]}>
                <Input disabled={loading} />
            </Form.Item>

            <Form.Item
                label="Nachname"
                name="lastname"
                rules={[{ required: true }]}>
                <Input disabled={loading} />
            </Form.Item>

            {/* Only require course input for non-directors */}
            {!isDirectorRegistration &&
                <Form.Item
                    label="Kurs"
                    name="course"
                    rules={[{ required: !isDirectorRegistration }]}>
                    <Select
                        disabled={loading}
                        showSearch>
                        {CourseOptionsList(courses)}
                    </Select>
                </Form.Item>
            }

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
    );
}

export default Register;