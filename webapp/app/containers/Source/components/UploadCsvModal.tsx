/*
 * <<
 * Davinci
 * ==
 * Copyright (C) 2016 - 2017 EDP
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */

import React, { useCallback, useState } from 'react'

import {
  Modal,
  Form,
  Input,
  Radio,
  Popover,
  Upload,
  Icon,
  Button,
  message
} from 'antd'
const FormItem = Form.Item
const RadioGroup = Radio.Group
import { FormItemProps, FormComponentProps } from 'antd/lib/form'
import { UploadProps } from 'antd/lib/upload'

import { ICSVMetaInfo } from '../types'

const formItemLayout: Partial<FormItemProps> = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 }
}

interface IUploadCsvModalProps extends FormComponentProps<ICSVMetaInfo> {
  visible: boolean
  sourceId: number
  uploading: boolean
  onValidate: (
    values: Pick<ICSVMetaInfo, 'sourceId' | 'tableName' | 'mode'>,
    callback: (errMsg?: string) => void
  ) => void
  onOk: (value: ICSVMetaInfo) => void
  onCancel: () => void
}

const UploadCsvModal: React.FC<IUploadCsvModalProps> = (props) => {
  const { visible, sourceId, uploading, form, onValidate, onOk, onCancel } = props
  const { getFieldDecorator } = form

  const [files, setFiles] = useState([])

  const preventUpload: UploadProps['beforeUpload'] = useCallback((file) => {
    setFiles([file])
    return false
  }, [])

  const save = useCallback(() => {
    if (!files.length) {
      message.error('请上传 csv 文件！')
      return
    }
    form.validateFields((err, values) => {
      if (err) {
        return
      }
      values.file = files[0]
      values.sourceId = sourceId
      onOk(values)
    })
  }, [sourceId, files, onOk])

  const validateTableName = useCallback(
    (_, tableName: string, callback) => {
      onValidate(
        {
          sourceId,
          tableName,
          mode: form.getFieldValue('mode')
        },
        callback
      )
    },
    [sourceId, onValidate]
  )

  const resetFields = useCallback(() => {
    form.resetFields()
    setFiles([])
  }, [])

  return (
    <Modal
      title="上传文件"
      visible={visible}
      maskClosable={false}
      afterClose={resetFields}
      confirmLoading={uploading}
      onOk={save}
      onCancel={onCancel}
    >
      <Form>
        <FormItem label="导入方式" {...formItemLayout}>
          {getFieldDecorator<ICSVMetaInfo>('mode', {
            initialValue: 0
          })(
            <RadioGroup>
              <Radio value={0}>更新</Radio>
            </RadioGroup>
          )}
          <Popover
            placement="right"
            content={
              <>
                <p>更新：根据设置的主键,若历史存在,则先删除历史再导入本次数据; 若历史不存在,则直接导入本次数据</p>
              </>
            }
          >
            <Icon type="question-circle-o" />
          </Popover>
        </FormItem>
        <FormItem label="上传" {...formItemLayout}>
          <Upload
            accept=".xlsx"
            multiple={false}
            fileList={files}
            beforeUpload={preventUpload}
          >
            <Button>
              <Icon type="upload" />
             上传文件
            </Button>
          </Upload>
        </FormItem>
      </Form>
    </Modal>
  )
}

export default Form.create<IUploadCsvModalProps>()(UploadCsvModal)
