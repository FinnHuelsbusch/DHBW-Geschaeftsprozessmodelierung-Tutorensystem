import { Button, ConfigProvider, DatePicker, Form, message, Modal } from "antd"
import { useForm } from "antd/lib/form/Form";
import TextArea from "antd/lib/input/TextArea";
import locale from 'antd/lib/locale/de_DE';
import Title from "antd/lib/skeleton/Title";
import 'moment/locale/de'
import { useState } from "react";
import { createTutorialOffer } from "../../api/api";
import { TutorialOffer } from "../../types/Tutorial";



interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}


const TutorialOfferModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [loading, setLoading] = useState(false);
    const [form] = useForm();

    const onFinish = (values: any) => {
        setLoading(true);
        createTutorialOffer({
            description: values.description,
            start: values.timerange[0].toDate(),
            end: values.timerange[1].toDate()
        } as TutorialOffer).then(res => {
            setLoading(false);
            message.success("Tutoriumsangebot erfolgreich erstellt.");
            setIsTutorialOfferModalVisible(false);
            form.resetFields()
        }, err => {
            setLoading(false);
            message.error("Tutoriumsangebot konnte nicht erstellt werden.");
        }

        )
    };

    const onCancel = () => {
        setIsTutorialOfferModalVisible(false)
        form.resetFields()
    }

    return (
        <ConfigProvider locale={locale}>
            <Modal
                destroyOnClose={true}
                visible={isModalVisible}
                onCancel={onCancel}
                title={"Tutoriumsangebot erstellen"}
                width={600}
                footer={[
                    <Button loading={loading} type="primary" onClick={e => form.submit()}>
                        Absenden
                    </Button>
                ]}
            >
                <Form
                    onFinish={onFinish}
                    form={form}
                    labelCol={{ span: 5 }}
                    wrapperCol={{ span: 17 }}
                >

                    <Form.Item
                        label="Zeitraum"
                        name="timerange"
                        rules={[{required: true}]}
                    >
                        <DatePicker.RangePicker
                            placeholder={["Anfang", "Ende"]}
                            format="DD.MM.YYYY"

                        />

                    </Form.Item>
                    <Form.Item
                        label="Beschreibung"
                        name="description"
                        rules={[{required: true}]}
                    >
                        <TextArea rows={4} placeholder="Maximal 500 Zeichen" maxLength={500} showCount />
                    </Form.Item>
                </Form>
            </Modal>
        </ConfigProvider>
    )
}

export default TutorialOfferModal;