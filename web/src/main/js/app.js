import {SketchField, Tools} from 'react-sketch';
import {PageHeader, Panel, Grid, Row, Col, Button, ButtonGroup, Label, Modal} from 'react-bootstrap';
import {Creatable} from 'react-select';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

var getImage = function () {
    var c = document.querySelector(".lower-canvas")
    return c.toDataURL();
}

class App extends React.Component {

    constructor(params) {
        super(params);

        this.onClear = this.onClear.bind(this);
        this.onPredict = this.onPredict.bind(this);
        this.state = {answer: null};
        client({method: 'GET', path: '/api/labels'}).then(response => {
            this.setState({labels: response.entity});
        });
    }

    onPredict() {
        var imgData = getImage();
        client({method: 'POST', path: '/api/predict', entity: imgData}).then(response => {
            this.setState({answer: response.entity});
        });
    }

    onClear() {
        this.sketch.clear();
        this.setState({answer: null});
    }

    render() {
        return (
            <Grid>
                <Row className="show-grid">
                    <Col xsHidden md={3}/>
                    <Col xs={12} md={6}>
                        <PageHeader>Teach me:
                            <small>Ask a question</small>
                        </PageHeader>
                    </Col>
                    <Col xsHidden md={3}/>
                </Row>
                <Row className="show-grid">
                    <Col xsHidden md={3}/>
                    <Col xs={8} md={4}>
                        <Panel>
                            <SketchField
                                height='300px'
                                ref={(c) => this.sketch = c}
                                tool={Tools.Pencil}
                                color='black'
                                lineWidth={3}/>
                        </Panel>
                        <Button bsStyle="default" onClick={this.onClear}>Clear</Button>
                    </Col>
                    <Col xs={4} md={2}>
                        {this.state.answer
                            ? <Answer parent={this} options={this.state.labels} value={this.state.answer}/>
                            : <Prompt parent={this} labels={this.state.labels}/>}
                    </Col>
                    <Col xsHidden md={3}/>
                </Row>
            </Grid>
        )
    }
}

class Prompt extends React.Component {
    render() {
        let labs = this.props.labels
            ? this.props.labels.map(c => <li key={c.value}>{c.label}</li>)
            : "";
        return (
            <div>
                <h3>{'Draw a simple shape and I will try to guess what it is. I know about: '}</h3>
                <h4>
                    <ul>
                        {labs}
                    </ul>
                </h4>
                <h3><Button bsStyle="primary" bsSize="large" onClick={this.props.parent.onPredict}>What is it?</Button></h3>
            </div>
        );
    }
}

class Answer extends React.Component {

    constructor(params) {
        super(params);

        this.onYes = this.onYes.bind(this);
        this.onNo = this.onNo.bind(this);
        this.closeYes = this.closeYes.bind(this);
        this.closeNo = this.closeNo.bind(this);
        this.state = {showYes: false, showNo: false, label: null};
    }

    onYes() {
        this.setState({showYes: true});
    }

    onNo() {
        this.setState({showNo: true});
    }

    closeYes() {
        this.props.parent.onClear();
        this.setState({showYes: false});
    }

    closeNo() {
        this.setState({showNo: false});
        let imgData = getImage();
        client({method: 'POST', path: '/api/fit?label=' + this.state.label.value, entity: imgData});
    }

    render() {
        let confidence = this.props.value.confidence;
        let title;
        if (confidence === 'SURE') {
            title = <div>
                        <h3>This is</h3>
                        <h3><Label bsStyle="primary">{this.props.value.labels[0]}</Label></h3>
                        <h3>I'm sure, right?</h3>
                    </div>
        } else if (confidence === 'UNCERTAIN') {
            title = <div>
                        <h3>I am not sure. It is either</h3>
                        <h3>
                            <Label bsStyle="primary">{this.props.value.labels[0]}</Label> or <Label bsStyle="primary">{this.props.value.labels[1]}</Label>
                        </h3>
                        <h3>What is it?</h3>
                    </div>
        } else {
            title = <h3>Oh! I have no idea what it is. Help me, please.</h3>
        }

        return (
            <div>
                {title}
                <ButtonGroup>
                    <Button bsStyle="primary" onClick={this.onYes}>Yes</Button>
                    <Button bsStyle="default" onClick={this.onNo}>No</Button>
                </ButtonGroup>

                <Modal show={this.state.showYes} onHide={this.closeYes}>
                    <Modal.Header closeButton>
                        <Modal.Title>Hurrah!</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <h4>I know, I am awesome</h4>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.closeYes}>Close</Button>
                    </Modal.Footer>
                </Modal>

                <Modal show={this.state.showNo} onHide={this.closeNo}>
                    <Modal.Header closeButton>
                        <Modal.Title>Oh no!</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <h4>Tell me what it was</h4>
                        <Creatable
                            name="form-field-name"
                            value={this.state.label}
                            onChange={value => this.setState({label: value})}
                            options={this.props.options}/>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button bsStyle="primary" onClick={this.closeNo}>Submit</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('react')
);
