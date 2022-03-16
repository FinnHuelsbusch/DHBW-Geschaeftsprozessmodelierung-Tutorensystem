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
import { PasswordFieldProps, PasswordWithConfirm } from '../inputs/PasswordInput';

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
        console.log("matching", PasswordWithConfirm.passwordsMatch(values.password, values.passwordConfirm));
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
            const specialisationCourseId = isDirectorRegistration ? undefined : parseInt(values.specialisationCourse);
            register({
                email: values.email.trim(),
                password: values.password,
                firstName: values.firstname,
                lastName: values.lastname,
                specialisationCourseId: specialisationCourseId
            })
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

    useEffect(() => {
        // re-focus email field to avoid losing focus after state change re-render
        form.getFieldInstance("email").focus();
    }, [isDirectorRegistration]);

    const onEmailInputChange = (e: any) => {
        if (isDirectorEmail(e.target.value)) {
            setIsDirectorRegistration(true);
            form.setFieldsValue({
                ...form.getFieldsValue(),
                specialisationCourse: undefined,
            });
        } else {
            setIsDirectorRegistration(false);
        }
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
                validator={PasswordWithConfirm.passwordConstraintValidator}
            />

            <PasswordWithConfirm.PasswordConfirm
                disabled={loading}
                validateStatus={passwordFieldsInfo.validateStatus}
                message={passwordFieldsInfo.message}
                validator={PasswordWithConfirm.passwordConstraintValidator}
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
            {/* {!isDirectorRegistration && */}
            <Form.Item
                label="Studienrichtung"
                name="specialisationCourse"
                tooltip={isDirectorRegistration ? "Personal muss keine Studienrichtung angeben" : undefined}
                rules={[{ required: !isDirectorRegistration }]}>
                <Select
                    disabled={loading || isDirectorRegistration}
                    optionFilterProp='textForFilter'
                    showSearch>
                    {courses.map(course =>
                        <Select.OptGroup label={`${course.title} (${course.abbreviation})`}>
                            {course.specialisationCourses.map(
                                specialisation => (
                                    <Select.Option
                                        key={`${specialisation.id}`}
                                        textForFilter={`${course.abbreviation} ${specialisation.abbreviation} ${course.title} ${specialisation.title}`}
                                    >
                                        {specialisation.title} <i style={{ color: 'gray' }}>({course.abbreviation} {specialisation.abbreviation})</i>
                                    </Select.Option>)
                            )}
                        </Select.OptGroup>)}
                </Select>
            </Form.Item>
            {/* } */}

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