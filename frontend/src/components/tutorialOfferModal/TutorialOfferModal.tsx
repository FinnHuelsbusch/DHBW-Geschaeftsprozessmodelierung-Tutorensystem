import { Button, DatePicker, Form, message, Modal } from "antd"
import { useForm } from "antd/lib/form/Form";
import TextArea from "antd/lib/input/TextArea";

import Title from "antd/lib/skeleton/Title";

import { useState } from "react";
import { validateMessages} from "../../utils/Messages";



interface Props {
    isModalVisible: boolean,
    setIsTutorialOfferModalVisible: (visible: boolean) => void
}


const TutorialOfferModal: React.FC<Props> = ({ isModalVisible, setIsTutorialOfferModalVisible }) => {

    const [loading, setLoading] = useState(false);
    const [form] = useForm();

    

    const onCancel = () => {
        setIsTutorialOfferModalVisible(false)
        form.resetFields()
    }

    return (

            <Modal
                destroyOnClose={true}
                visible={isModalVisible}
                onCancel={onCancel}
                title={"Tutoriumsangebot erstellen"}
                width={600}
                footer={[
                    <Button
                        loading={loading}
                        type="primary"
                        htmlType="submit"
                        onClick={e => form.submit()}>
                        Absenden
                    </Button>
                ]}
            >
                <Form
                    
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
    )
}

export default TutorialOfferModal;