import { Col, Descriptions, Modal, Row, Tag, Typography } from 'antd';
import Paragraph from 'antd/lib/typography/Paragraph';
import Title from 'antd/lib/typography/Title';
import React from 'react';
import { Tutorial } from '../../types/Tutorial';
import { formatDate } from '../../utils/DateTimeHandling';

type Props = {
    tutorial: Tutorial | undefined,
    onClose: () => void,
}

const TutorialDetails: React.FC<Props> = ({ tutorial, onClose }) => {

    const getDetailsRow = (label: string, content: any) => {
        return (
            <Row>
                <Col flex="100px">{label}</Col>
                <Col flex="auto">{content}</Col>
            </Row>
        );
    }

    return (
        <Modal
            visible={tutorial ? true : false}
            onCancel={onClose}
            title={tutorial?.title}
        >
            {tutorial &&
                <Typography>
                    <Title level={4}>Inhalt</Title>
                    <Paragraph>{tutorial.description}</Paragraph>

                    <Title level={4}>Für Studiengänge</Title>
                    <Paragraph>
                        {tutorial.specialisationCourses.map(course => (
                            <Tag>{course.specialization}</Tag>
                        ))}
                    </Paragraph>

                    <Title level={4}>Ablauf</Title>
                    <Paragraph>
                        {getDetailsRow("Umfang", tutorial.durationMinutes)}
                        {getDetailsRow("Zeitraum", `${formatDate(tutorial.start)} - ${formatDate(tutorial.end)}`)}
                    </Paragraph>
                </Typography>
            }
        </Modal>
    );
};

export default TutorialDetails;