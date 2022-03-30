import { Button, Divider, Form, Input, message, Select, Tooltip } from 'antd';
import React, { useContext, useEffect, useState } from 'react';
import { LockOutlined } from '@ant-design/icons';
import { CourseWithTitleAndLeaders, CourseWithTitleAndSpecialisations, SpecialisationCourse } from '../../types/Course';
import { AuthContext } from '../../context/UserContext';
import EmailFormInput from '../inputs/EmailFormInput';
import ChangePasswordModal from './PasswordChangeModal';
import { UserRole } from '../../types/User';
import FormText from '../inputs/FormText';
import { getCoursesWithTitleAndSpecialisations, getRequestError, updateUser } from '../../api/api';
import { getErrorMessageString } from '../../types/RequestError';
import { useForm } from 'antd/lib/form/Form';


const Settings: React.FC = () => {

    const [courses, setCourses] = useState<CourseWithTitleAndSpecialisations[]>([]);
    const [loading, setLoading] = useState(false);
    const authContext = useContext(AuthContext);
    const [showChangePasswordModal, setShowChangePasswordModal] = useState(false);
    const isStudentLoggedIn = authContext.loggedUser?.roles.at(0) === UserRole.ROLE_STUDENT ? true : false;
    const [form] = useForm();

    useEffect(() => {
        getCoursesWithTitleAndSpecialisations().then(courses => {
            setCourses(courses);
        }, err => {
            message.error(getErrorMessageString(getRequestError(err).errorCode))
        });
    }, []);

    const onSubmit = (values: any) => {
        setLoading(true);
        const specialisationCourseId = !isStudentLoggedIn ? undefined : parseInt(values.specialisationCourse);
        updateUser({
            email: authContext.loggedUser?.email,
            firstName: values.firstname,
            lastName: values.lastname,
            specialisationCourseId: specialisationCourseId
        })
            .then(res => {
                message.success("Ihre Daten wurden erfolgreich geändert");
                setLoading(false);
            }, err => {
                const reqErr = getRequestError(err);
                message.error(getErrorMessageString(reqErr.errorCode));
                setLoading(false);
            });
        resetFormData();    
    }; 

    const resetFormData = () => {
       form.resetFields();
    } 

    return(
        <>
            <Form
                form={form}
                name="settings"
                labelCol={{ span: 8 }}
                initialValues={{ email: authContext.loggedUser?.email }}
                wrapperCol={{ span: 10 }}
                onFinish={onSubmit}>
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
                <FormText
                    label="Vorname"                  
                    name="firstname"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input />
                </FormText>
                <FormText
                    label="Nachname"
                    name="lastname"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input />
                </FormText>
                <Form.Item
                label="Studienrichtung"
                name="specialisationCourse"
                tooltip={!isStudentLoggedIn ? "Personal muss keine Studienrichtung angeben" : undefined}
                rules={[{ required: isStudentLoggedIn }]}>
                <Select
                    disabled={loading || !isStudentLoggedIn}
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

                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <Button loading={loading} htmlType='submit' type='primary'>
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