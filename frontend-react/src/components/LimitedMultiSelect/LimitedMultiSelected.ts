import { CloseOutlined, PlusOutlined } from '@ant-design/icons';
// import {
//     Button,
//     Col,
//     Divider,
//     Form,
//     Input,
//     Row,
//     Select, SelectProps,
//     Space,
//     Steps,
//     Switch,
//     Tabs,
//     TabsProps,
//     Tag,
//     Typography
// } from 'antd';
// import {useCallback, useMemo, useState} from 'react';
//
//
// function toRawValue(optionValue:any) {
//     return typeof optionValue === "object" ? optionValue.value : optionValue;
// }
// type LimitedSelectProps = SelectProps & {maxSelected: number}
// export function MultiSelect({
//                                 maxSelected,
//                                 options: originalOptions,
//                                 onChange: originalOnChange,
//                                 ...selectProps
//                             }: LimitedSelectProps) {
//     const [selectedValues, setSelectedValues] = useState([]);
//
//     // Proxy options to conditionally disable them
//     const options = useMemo(() => {
//         if (originalOptions === undefined) {
//             return originalOptions;
//         }
//
//         const isMaxSelected = selectedValues.length === maxSelected;
//
//         // @ts-ignore
//         return originalOptions.map((option) => ({
//             ...option,
//             // Disable/enable unselected items to prevent/allow selection
//             ...(!selectedValues.includes(option.value) && {
//                 disabled: isMaxSelected,
//             }),
//         }));
//     }, [originalOptions, maxSelected, selectedValues]);
//
//     const onChange = useCallback<any>(
//         (values, option) => {
//             setSelectedValues(values.map(toRawValue));
//
//             originalOnChange?.(values, option);
//         },
//         [originalOnChange],
//     );
//
//     return (
//         <Select
//             {...selectProps}
//     options={options}
//     mode="multiple"
//     onChange={onChange}
//     />
// );
// }