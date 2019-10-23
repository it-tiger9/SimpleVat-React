import React from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { Card, CardHeader, CardBody, Button, Input, Form, FormGroup, Label } from 'reactstrap'
import { toast } from 'react-toastify'
import _ from "lodash"
import Loader from "components/loader"

import 'react-toastify/dist/ReactToastify.css'
import './style.scss'

import * as VatActions from './actions'


const mapStateToProps = (state) => {
  return ({
    vat_row: state.vat.vat_row
  })
}
const mapDispatchToProps = (dispatch) => {
  return ({
    vatActions: bindActionCreators(VatActions, dispatch)
  })
}

class CreateOrEditVatCategory extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      vatData: {},
      loading: false
    }

    this.handleChange = this.handleChange.bind(this)
    this.handleSubmit = this.handleSubmit.bind(this)
    this.success = this.success.bind(this)
  }

  componentDidMount() {
    const params = new URLSearchParams(this.props.location.search)
    const id = params.get("id")

    if (id) {
      this.setState({ loading: true });
      this.props.vatActions.getVatByID(id).then(res => {
        if (res.status === 200)
          this.setState({ loading: false })
      })
    }
  }

  componentWillReceiveProps(newProps) {
    if (this.props.vat_row !== newProps.vat_row) {
      this.setState({
        vatData: newProps.vat_row
      })
    }
  }

  // Save Updated Field's Value to State
  handleChange(e, name) {
    this.setState({
      vatData: _.set(
        { ...this.state.vatData },
        e.target.name && e.target.name !== "" ? e.target.name : name,
        e.target.type === "checkbox" ? e.target.checked : e.target.value
      )
    })
  }

  // Show Success Toast
  success() {
    return toast.success("Vat Category Updated successfully... ", {
      position: toast.POSITION.TOP_RIGHT
    })
  }

  // Create or Edit Vat
  handleSubmit(e) {
    e.preventDefault()

    this.setState({ loading: true })
    const { name, vat, id } = this.state.vatData

    let postObj

    if (id) {
      postObj = { ...this.state.vatData }
    } else {
      postObj = { name, vat }
    }

    this.props.vatActions.createBat(postObj).then(res => {
      if (res.status === 200) {
        this.success()
        this.props.history.push("/admin/settings/vat-category")
      }
    })
  }

  render() {
    const { loading } = this.state
    const { id, name, vat } = this.state.vatData ? this.state.vatData : {}

    return (
      <div className="vat-category-create-screen">
        <div className="animated">
          <Card>
            <CardHeader>
              {id ? "Edit Vat Category" : "New Vat Category"}
            </CardHeader>
            <CardBody>
              <Form onSubmit={this.handleSubmit} name="simpleForm">
                <FormGroup>
                  <Label htmlFor="name">Vat Category Name</Label>
                  <Input
                    type="text"
                    id="name"
                    name="name"
                    defaultValue={name}
                    placeholder="Enter Vat Category Name"
                    onChange={this.handleChange}
                    required
                  />
                </FormGroup>
                <FormGroup>
                  <Label htmlFor="name">Percentage</Label>
                  <Input
                    type="number"
                    id="name"
                    name="vat"
                    defaultValue={vat}
                    placeholder="Enter Percentage"
                    onChange={this.handleChange}
                    required
                  />
                </FormGroup>            
                <FormGroup className="text-right">
                  <Button type="submit" color="secondary" onClick={() => {this.props.history.push("/admin/settings/vat-category")}}>
                    <i className="fa fa-ban"></i> Cancel
                  </Button>
                  <Button type="submit" color="primary">
                    <i className="fa fa-dot-circle-o"></i> Save
                  </Button>
                </FormGroup>
              </Form>
            </CardBody>
          </Card>
          {loading ? (
            <Loader></Loader>
          ) : (
              ""
            )}
        </div>
      </div>
    )
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(CreateOrEditVatCategory)