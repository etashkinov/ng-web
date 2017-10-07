import {SketchField, Tools} from 'react-sketch';
import {PageHeader,Panel,Grid,Row,Col,Button,ButtonGroup,Label,Modal} from 'react-bootstrap';

const React = require('react');
const ReactDOM = require('react-dom');
const Rest = require('rest');

class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <SketchFieldDemo/>
        )
    }
}

class SketchFieldDemo extends React.Component {
     constructor(params) {
        super(params);

        this.onClear = this.onClear.bind(this);
        this.onEvaluate = this.onEvaluate.bind(this);
        this.state = { answer: null };
     }

     onEvaluate() {
        var c = document.querySelector(".lower-canvas")
        var imgData=c.getContext("2d").getImageData(0,0,c.width,c.height);
        Rest({method: 'POST', path: '/api/evaluate', entity: imgData.data}).done(response => {
           this.setState({ answer: response.entity });
        });
      }

     onClear() {
        this._sketch.clear();
        this.setState({ answer: null });
     }

     render() {
        return (
            <Grid>
                <Row className="show-grid">
                    <Col xsHidden md={3}></Col>
                    <Col xs={12} md={6}>
                        <PageHeader>Teach me: <small>Ask a question</small></PageHeader>
                    </Col>
                    <Col xsHidden md={3}></Col>
                </Row>
                <Row className="show-grid">
                      <Col xsHidden md={3}></Col>
                      <Col xs={8} md={4}>
                            <Panel>
                                <SketchField
                                             height='300px'
                                             ref={(c) => this._sketch = c}
                                               tool={Tools.Pencil}
                                               color='black'
                                               lineWidth={3}/>
                            </Panel>
                           <Button bsStyle="default" onClick={this.onClear}>Clear</Button>
                      </Col>
                      <Col xs={4} md={2}>
                        <h3>{'Draw a simple shape and I will try to guess what it is.'}</h3>
                        { this.state.answer
                            ? <Answer value={this.state.answer} parent={this}/>
                            : <Button bsStyle="primary" onClick={this.onEvaluate}>What is it?</Button> }
                      </Col>
                      <Col xsHidden md={3}></Col>
                </Row>
            </Grid>
        )
     }
}

class Answer extends React.Component {

    constructor(params) {
        super(params);

        this.onYes = this.onYes.bind(this);
        this.onNo = this.onNo.bind(this);
        this.closeYes = this.closeYes.bind(this);
        this.closeNo = this.closeNo.bind(this);
        this.state = { showYes: false, showNo: false };
    }

    onYes() {
        this.setState({ showYes: true });
    }

    onNo() {
        this.setState({ showNo: true });
    }

    closeYes() {
        this.setState({ showYes: false });
        this.props.parent.onClear();
    }

    closeNo() {
        this.setState({ showNo: false });
    }

    render() {
        return (
            <div>
                <h2><Label bsStyle="primary">{this.props.value}</Label></h2>
                <h3>Am I right?</h3>
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
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.closeYes}>Close</Button>
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
