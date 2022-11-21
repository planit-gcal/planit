import React, { useContext, useEffect, useRef, useState } from 'react';
import { Form, Table, InputRef } from 'antd';
import type { FormInstance } from 'antd/es/form';
import DatePicker from "../DatePicker/DatePicker";

const EditableContext = React.createContext<FormInstance<any> | null>(null);

interface Item {
    key: string;
    name: string;
    exclude: boolean;
    availability: [string, string];
}

interface EditableRowProps {
    index: number;
}

const EditableRow: React.FC<EditableRowProps> = ({ index, ...props }) => {
    const [form] = Form.useForm();
    return (
        <Form form={form} component={false}>
            <EditableContext.Provider value={form}>
                <tr {...props} />
            </EditableContext.Provider>
        </Form>
    );
};

interface EditableCellProps {
    title: React.ReactNode;
    editable: boolean;
    children: React.ReactNode;
    dataIndex: keyof Item;
    record: Item;
    handleSave: (record: Item) => void;
}

const EditableCell: React.FC<EditableCellProps> = ({
                                                       title,
                                                       editable,
                                                       children,
                                                       dataIndex,
                                                       record,
                                                       handleSave,
                                                       ...restProps
                                                   }) => {
    const [editing, setEditing] = useState(false);
    const inputRef = useRef<InputRef>(null);
    const form = useContext(EditableContext)!;

    useEffect(() => {
        if (editing) {
            inputRef.current!.focus();
        }
    }, [editing]);

    const toggleEdit = () => {
        setEditing(!editing);
        form.setFieldsValue({ [dataIndex]: record[dataIndex] });
    };

    let childNode = children;

    if (editable) {
        childNode = editing ? (
            <Form.Item
                style={{ margin: 0 }}
                name={dataIndex}
                rules={[
                    {
                        required: true,
                        message: `${title} is required.`,
                    },
                ]}
            >
                <DatePicker.RangePicker style={{ width: '100%' }} picker="time" format={'HH:mm'}/>
            </Form.Item>
        ) : (
            <div className="editable-cell-value-wrap" style={{ paddingRight: 24 }} onClick={toggleEdit}>
                {children}
            </div>
        );
    }

    return <td {...restProps}>{childNode}</td>;
};

type EditableTableProps = Parameters<typeof Table>[0];

interface DataType {
    key: string;
    name: string;
    exclude: boolean;
    availability: [string, string];
}

type ColumnTypes = Exclude<EditableTableProps['columns'], undefined>;

const ExcludeTable: React.FC = () => {
    const [dataSource, setDataSource] = useState<DataType[]>([
        {
            key: '0',
            name: 'Monday',
            exclude: true,
            availability: ['08:00', '16:00'],
        },
        {
            key: '1',
            name: 'Tuesday',
            exclude: true,
            availability: ['08:00', '16:00'],
        },
        {
            key: '2',
            name: 'Wednesday',
            exclude: true,
            availability: ['08:00', '16:00'],
        },
        {
            key: '3',
            name: 'Thursday',
            exclude: true,
            availability: ['08:00', '16:00'],
        },
        {
            key: '4',
            name: 'Friday',
            exclude: true,
            availability: ['08:00', '16:00'],
        },
        {
            key: '5',
            name: 'Saturday',
            exclude: false,
            availability: ['12:00', '23:00'],
        },
        {
            key: '6',
            name: 'Sunday',
            exclude: false,
            availability: ['12:00', '23:00'],
        },
    ]);

    const defaultColumns: (ColumnTypes[number] & { editable?: boolean; dataIndex: string })[] = [
        {
            title: 'Name',
            dataIndex: 'name',
            width: '30%',
        },
        {
            title: 'Exclude',
            dataIndex: 'exclude',
        },
        {
            title: 'Availability',
            dataIndex: 'availability',
        },
    ];

    const handleSave = (row: DataType) => {
        const newData = [...dataSource];
        const index = newData.findIndex((item) => row.key === item.key);
        const item = newData[index];
        newData.splice(index, 1, {
            ...item,
            ...row,
        });
        setDataSource(newData);
    };

    const components = {
        body: {
            row: EditableRow,
            cell: EditableCell,
        },
    };

    const columns = defaultColumns.map((col) => {
        if (!col.editable) {
            return col;
        }
        return {
            ...col,
            onCell: (record: DataType) => ({
                record,
                editable: col.editable,
                dataIndex: col.dataIndex,
                title: col.title,
                handleSave,
            }),
        };
    });

    return (
        <div>
            <Table
                components={components}
                rowClassName={() => 'editable-row'}
                bordered
                dataSource={dataSource}
                columns={columns as ColumnTypes}
            />
        </div>
    );
};

export default ExcludeTable;