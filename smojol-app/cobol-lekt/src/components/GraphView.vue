<script lang="ts">

import cytoscape from "cytoscape";
import cydagre from "cytoscape-dagre";
import {asCytoscapeTree, TranspilerNodeChildrenAccess, TreeModelNode} from "@/ts/CytoTree";
import {defineComponent, PropType, ref} from "vue";
import {asCytoscapeDigraph, Digraph} from "@/ts/Digraph";
import {CytoModel} from "@/ts/CytoscapeTypes";
import {MutableCenter} from "@/ts/FlippableId";
import {LoopBody, LoopNode} from "@/ts/LoopTypes";
import {randomColour} from "@/ts/Colours";

export default defineComponent({
      name: "GraphView",
      props: {
        intermediateAst: {
          type: [Object, null] as PropType<TreeModelNode | null>,
          required: true
        },
        loopBodies: {
          type: Object as PropType<LoopNode[]>,
          required: true
        },
        intermediateCfgDigraph: {
          type: [Object, null] as PropType<Digraph | null>,
          required: true
        },
        centerNode: {
          type: [Object, null] as PropType<MutableCenter | null>,
          required: true
        },
        t1t2Result: {
          type: [Object, null] as PropType<{ isReducible: boolean } | null>,
          required: true
        },
        flowModel: {
          type: [Object, null] as PropType<Digraph | null>,
          required: true
        }
      },
      setup() {
        const cy = ref<cytoscape.Core | null>(null);
        return {cy};
      },
      mounted() {
        // this.buildGraph();
      },
      computed: {
        isReducible() {
          if (this.t1t2Result === null) return "";
          return this.t1t2Result.isReducible ? "Reducible" : "Irreducible";
        }
      },
      watch: {
        intermediateAst(newValue: TreeModelNode) {
          this.buildGraph(asCytoscapeTree(newValue, TranspilerNodeChildrenAccess));
        },
        intermediateCfgDigraph(newValue: Digraph) {
          this.buildGraph(asCytoscapeDigraph(newValue));
        },
        flowModel(newValue: Digraph) {
          this.buildGraph(asCytoscapeDigraph(newValue));
        },
        centerNode(newValue: MutableCenter) {
          if (this.cy === null) return;
          const elementById = this.cy.getElementById(newValue.id);
          this.cy.elements().not(elementById).unselect();
          this.cy.center(elementById);
          elementById.select();
        },
        loopBodies(loopBodies: LoopBody[]) {
          if (this.cy === null) return;
          loopBodies.forEach(body => {
            const bodyColour = randomColour();
            const allLoopNodeIDs = body.loopNodes.map(ln => this.cy!.getElementById(ln.id));
            allLoopNodeIDs.forEach(ele => ele.style({
              "background-color": bodyColour,
              borderWidth: "1px",
              borderColor: "black"
            }));
          })
        }
      },
      methods: {
        buildGraph(cytoscapeModel: CytoModel) {
          cydagre(cytoscape);
          this.cy = cytoscape({
            container: document.getElementById("cyto"),
            elements: cytoscapeModel,
            style: [
              {
                selector: 'node',
                style: {
                  'background-color': "purple",
                  'label': 'data(id)',
                  "transition-property": "background-color",
                  'transition-duration': 500,
                  "transition-timing-function": "ease-in-out",
                }
              },
              {
                selector: 'node:selected',
                style: {
                  "border-color": "red",
                  "border-width": "2px",
                  'background-color': 'yellow',
                  'label': 'data(id)',
                  "transition-property": "background-color",
                  'transition-duration': 500,
                  "transition-timing-function": "ease-in-out",
                }
              },
              {
                selector: 'edge',
                style: {
                  'width': 3,
                  'line-color': '#ccc',
                  'target-arrow-color': '#ccc',
                  'target-arrow-shape': 'triangle',
                  'curve-style': 'bezier'
                }
              }
            ],

            layout: {
              name: 'breadthfirst',
              directed: true
            }
          });
          this.cy.on('select', 'node', (event) => {
            const node = event.target;
            // console.log(`Node selected: ${node.target.id}`);
            const nodeData = node.data();
            this.$emit("node-details-changed", nodeData);
          });
          this.cy.center();
        }
      }
    }
)</script>

<template>
  <div class="headered-pane" id="graph-view">
    <div class="pane-heading">Graph View <span v-if="t1t2Result != null">({{ isReducible }})</span></div>
    <div id="cyto" class="cyto"></div>
  </div>
</template>

<style scoped>
#cyto {
  height: 100%;
  background-color: azure;
}

.flashingnode {
  border: 3px solid red;
  background-color: yellow;
  transition: background-color 3s ease;
}
</style>
