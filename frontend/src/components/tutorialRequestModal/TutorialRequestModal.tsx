import { Form, Modal, Input, Button, message, Select } from 'antd';
import TextArea from "antd/lib/input/TextArea";
import { useForm } from "antd/lib/form/Form";
import { useState } from "react";
import { createTutorialRequest } from "../../api/api";
import { TutorialRequest } from "../../types/Tutorial";
import { validateMessages } from "../../utils/Messages";

interface Props {
    isModalVisible: boolean,
    setIsTutorialRequestModalVisible: (visible: boolean) => void
}

export const TutorialRequestModal: React.FC<Props> = ({ isModalVisible, setIsTutorialRequestModalVisible }) => {

    const [loading, setLoading] = useState(false);
    const [form] = useForm();
    const { Option } = Select;

    const onFinish = (values: any) => {
        console.log(values.semester);
        setLoading(true);
        createTutorialRequest({
            title: values.title,
            semester: Number(values.semester),
            description: values.description
        } as TutorialRequest).then(res => {
            setLoading(false);
            message.success("Tutoriumsanfrage erfolgreich erstellt.");
            setIsTutorialRequestModalVisible(false);
            form.resetFields()
        }, err => {
            setLoading(false);
            message.error("Tutoriumsanfrage konnte nicht erstellt werden.");
        }

        )
    };

    const onCancel = () => {
        setIsTutorialRequestModalVisible(false)
        form.resetFields()
    }

    return (
        <Modal
            title="Tutoriumsanfrage erstellen"
            destroyOnClose={true}
            visible={isModalVisible}
            onCancel={onCancel}
            width={600}
            footer={[
                <Button loading={loading} htmlType="submit" type="primary" onClick={e => form.submit()}>
                    Absenden
                </Button>
            ]}>
            <Form
                onFinish={onFinish}
                form={form}
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 17 }}
                validateMessages={validateMessages}
            >
                <Form.Item label="Titel" name="title" rules={[{ required: true },{whitespace: true}]}>
                    <Input placeholder="Titel der Vorlesung" allowClear disabled={loading} />
                </Form.Item>
                <Form.Item label="Semester" name="semester" rules={[{required:true}]}>
                    <Select
                        showSearch
                        placeholder="Semester wählen"
                        disabled = {loading}
                        allowClear
                    >
                        {Array.from(Array(6).keys()).map(i => (
                            <Select.Option key={i + 1} value={i + 1}>
                                {i + 1}
                            </Select.Option>
                        ))}
                    </Select>
                </Form.Item >
                <Form.Item label="Spezialisierung" name="specialisation" rules={[{required:true}]}>
                    <Select
                        showSearch
                        placeholder="Spezialsierung wählen"
                        disabled = {loading}
                        allowClear
                    >
                        {Array.from(Array(6).keys()).map(i => (
                            <Select.Option key={i + 1} value={i + 1}>
                                {i + 1}
                            </Select.Option>
                        ))}
                    </Select>
                </Form.Item >
                    
                <Form.Item label="Beschreibung" name="description" rules={[{required: true}]}>
                    <TextArea rows={4} placeholder="Beschreiben Sie hier, bei welchen Themen Sie Unterstützung brauchen." maxLength={500} showCount disabled={loading}/>
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default TutorialRequestModal;