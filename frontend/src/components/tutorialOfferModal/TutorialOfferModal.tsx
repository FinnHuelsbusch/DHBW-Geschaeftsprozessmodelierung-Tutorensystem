import { Button, DatePicker, Dropdown, Form, Menu, message, Modal, Select } from "antd"
import { useForm } from "antd/lib/form/Form";
import { DownOutlined } from '@ant-design/icons';



import { useEffect, useState } from "react";
import { getCourses } from "../../api/api";
import { Course } from "../../types/Course";




interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}


const TutorialOfferModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [loading, setLoading] = useState(false);
    const [selectedCourse, setselectedCourse] = useState<Course>()
    const [courses, setCourses] = useState<Course[]>([]);


    const onFinish = (values: any) => {
        setIsTutorialOfferModalVisible(false)
    };

    const onCancel = () => {
        setIsTutorialOfferModalVisible(false)
    }

    useEffect(() => {
        // initial opening of page: log in user if persisted
        getCourses().then(Courses => {
            setCourses(Courses);
        }, err => {
            message.error("Böses hui")
        });
    }, []);



    return (

        <Modal
            destroyOnClose={true}
            visible={isModalVisible}
            onCancel={onCancel}
            title={"Tutoriumsangebot erstellen"}
            width={600}
            footer={[
                <a href={"mailto:"+selectedCourse?.leadBy.map(user => {return user.email;}).concat() + "?body=Ein noch zu schreibender Text."} >
                    <Button
                        loading={loading}
                        type="primary"
                        htmlType="submit"
                        onClick={onFinish}
                        disabled={selectedCourse? false: true}>
                        Kontaktieren
                    </Button>
                </a>
            ]}
        >

            <div>
                Für welchen Studiengang möchtest du ein Tutorium anbieten:<br></br>
                <Select 
                    placeholder="Studiengang wählen"
                    onSelect={(selectedCourse: String) => {setselectedCourse(courses.find((course) => {return course.title === selectedCourse}))}}
                >
                    {courses.map(course => (
                        <Select.Option key={course.id} value={course.title}>
                            {course.title}
                        </Select.Option>
                    ))}
                </Select>
            </div>

        </Modal>
    )
}

export default TutorialOfferModal;