import { Button, DatePicker, Form, Input, message, Modal, Select, TreeSelect } from "antd"
import { useForm } from "antd/lib/form/Form";
import { useEffect, useState } from "react";
import { getCoursesWithTitleAndSpecialisations, getRequestError, getUsersWithNameAndMailAndId, putTutorial, updateTutorial } from "../../api/api";
import { CourseWithTitleAndSpecialisations } from "../../types/Course";
import TextArea from "antd/lib/input/TextArea";
import { UserWithMailAndNameAndId } from "../../types/User";
import { getErrorMessageString } from "../../types/RequestError";
import { TreeNode } from "antd/lib/tree-select";
import moment from "moment";
import { isValidEmail } from "../inputs/EmailFormInput";
import { useNavigate } from "react-router-dom";
import { Tutorial } from "../../types/Tutorial";


interface Props {
    isModalVisible: boolean,
    setIsTutorialCreateModalVisible: (visible: boolean, isUpdated: boolean) => void,
    existingTutorial?: Tutorial
}

const TutorialCreateModal: React.FC<Props> = ({ isModalVisible, setIsTutorialCreateModalVisible, existingTutorial }) => {

    const [users, setUsers] = useState<UserWithMailAndNameAndId[]>([]);
    const [form] = useForm();
    const [courses, setCourses] = useState<CourseWithTitleAndSpecialisations[]>([]);
    const navigate = useNavigate();

    const onFinish = (values: any) => {
        const durationMinutes = parseInt(values.durationMinutes.split(":")[0]) * 60 + parseInt(values.durationMinutes.split(":")[1]);
        const tutorial = {
            title: values.title,
            description: values.description,
            start: moment(values.timerange[0]).format("YYYY-MM-DD"),
            end: moment(values.timerange[1]).format("YYYY-MM-DD"),
            durationMinutes: durationMinutes,
            tutorEmails: values.tutorEmails,
            specialisationCoursesIds: values.specialisationCoursesIds,
            appointment: values.appointment
        }
        if (existingTutorial) {
            updateTutorial(existingTutorial.id, tutorial ).then(tutorialId => {
                setIsTutorialCreateModalVisible(false, true);
                form.resetFields();
                message.success("Tutorium erfolgreich bearbeitet");
                navigate(`/tutorials/${tutorialId}`);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });
        } else {
            putTutorial(tutorial).then(tutorialId => {
                setIsTutorialCreateModalVisible(false, false);
                form.resetFields();
                message.success("Tutorium erfolgreich erstellt");
                navigate(`/tutorials/${tutorialId}`);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });
        }


    };

    const onCancel = () => {
        setIsTutorialCreateModalVisible(false,false);
        form.resetFields();
    }

    useEffect(() => {
        if (isModalVisible) {
            getCoursesWithTitleAndSpecialisations().then(courses => {
                setCourses(courses);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });

            getUsersWithNameAndMailAndId().then(users => {
                setUsers(users);
            }, err => {
                message.error(getErrorMessageString(getRequestError(err).errorCode))
            });

            if (existingTutorial) {
                // activate changemode: fill existingTutorial into form
                form.setFieldsValue({
                    title: existingTutorial.title,
                    description: existingTutorial.description,
                    timerange: [moment(existingTutorial.start, "YYYY-MM-DD"), moment(existingTutorial.end, "YYYY-MM-DD")],
                    durationMinutes: Math.floor(existingTutorial.durationMinutes/60) + ":" + (existingTutorial.durationMinutes%60 < 10? '0': '') + existingTutorial.durationMinutes%60 ,
                    tutorEmails: existingTutorial.tutors.map(t => t.email),
                    specialisationCoursesIds: existingTutorial.specialisationCourses.map(s => s.id),
                    appointment: existingTutorial.appointment
                });
            }
        }
    }, [isModalVisible]);

    const validateTutorEmails = (rule: any, value: string[], callback: any) => {
        if (!value) {
            callback("Pflichtfeld");
            return;
        }
        value.forEach(element => {
            if (!isValidEmail(element)) {
                callback("Ungültiges Format");
                return;
            }
        });
        callback();
        return;
    };

    const validateDuration = (rule: any, value: String, callback: any) => {
        const durationPattern = /^\d{1,2}:\d{2}$/g

        if (!value) {
            callback("Pflichtfeld");
            return;
        }
        if (value.match(durationPattern)) {
            const hours = parseInt(value.split(":")[0]);
            const minutes = parseInt(value.split(":")[1]);
            if (minutes < 60 && !(minutes === 0 && hours === 0)) {
                callback();
                return;
            }
        }
        callback("Ungültiges Format");
        return;
    };



    return (

        <Modal
            visible={isModalVisible}
            onCancel={onCancel}
            title={existingTutorial ? "Tutorium bearbeiten" : "Tutorium erstellen"}
            width={900}
            footer={[
                <Button
                    type="primary"
                    htmlType="submit"
                    onClick={e => form.submit()}>
                    {existingTutorial ? "Bearbeiten" : "Erstellen"}
                </Button>
            ]}
        >

            <div>
                <Form
                    form={form}
                    labelWrap
                    labelCol={{ span: 6 }}
                    wrapperCol={{ span: 18 }}
                    onFinish={onFinish}
                >

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Titel:"
                        name="title"
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Beschreibung:"
                        name="description"
                    >
                        <TextArea rows={4} placeholder="Maximal 500 Zeichen" maxLength={500} showCount />
                    </Form.Item>

                    <Form.Item
                        label="Zeitraum:"
                        name="timerange"
                        rules={[{ required: true }]}
                    >
                        <DatePicker.RangePicker
                            placeholder={["Anfang", "Ende"]}
                            format="DD.MM.YYYY"
                            disabledDate={(current) => current && current < moment().startOf('day')}
                        />
                    </Form.Item>

                    <Form.Item
                        label="Gestamumfang:"
                        name="durationMinutes"
                        rules={[{
                            required: true,
                            validator: validateDuration
                        }]}
                    >
                        <Input
                            placeholder="HH:MM"
                        />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Termine:"
                        name="appointment"
                    >
                        <TextArea rows={4} placeholder="Termine des Tutoriums eintragen." maxLength={500} showCount />
                    </Form.Item>

                    <Form.Item
                        rules={[{ required: true }]}
                        label="Studiengänge:"
                        name="specialisationCoursesIds"

                    >
                        <TreeSelect
                            filterTreeNode
                            treeNodeFilterProp="title"
                            treeCheckable={true}
                            showCheckedStrategy={TreeSelect.SHOW_CHILD}
                        >
                            {courses.map(course => (
                                <TreeNode title={course.title} value={course.title}>
                                    {course.specialisationCourses.map(specialisationCourse => (
                                        <TreeNode key={specialisationCourse.id} title={specialisationCourse.title} value={specialisationCourse.id} />
                                    ))}
                                </TreeNode>
                            ))}
                        </TreeSelect>
                    </Form.Item>
                    <Form.Item
                        label="Tutoren:"
                        name="tutorEmails"
                        rules={[{
                            required: true,
                            validator: validateTutorEmails
                        }]}
                    >
                        <Select mode="tags" tokenSeparators={[" ", ","]} placeholder="Bitte auswählen oder Adresse einfügen" >
                            {users.map(user => (
                                <Select.Option key={user.id} value={user.email}>
                                    {user.firstName} {user.lastName} ({user.email})
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </div>

        </Modal>
    )
}

export default TutorialCreateModal;