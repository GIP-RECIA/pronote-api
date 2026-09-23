<!--
 Copyright © 2026 GIP-RECIA (https://www.recia.fr/)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
</script>

<template>
  <div class="fiche-eleve-skeleton" aria-hidden="true">
    <div class="stats-strip">
      <div v-for="n in 5" :key="n" class="stat-tile r-card">
        <span class="skeleton num" />
        <span class="skeleton lbl" />
      </div>
    </div>

    <div class="layout">
      <div class="main-col">
        <div class="r-card">
          <span class="skeleton section-title" />
          <span class="skeleton line" />
          <span class="skeleton line" />
          <span class="skeleton line short" />
        </div>
        <div class="r-card travail-a-faire">
          <span class="skeleton section-title" />
          <span class="skeleton line" />
          <span class="skeleton line short" />
        </div>
      </div>

      <div class="sidebar-col">
        <div v-for="n in 4" :key="n" class="r-card">
          <span class="skeleton section-title" />
          <span class="skeleton line" />
          <span class="skeleton line short" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.fiche-eleve-skeleton {
  .stats-strip {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 12px;
    margin-bottom: 24px;

    .stat-tile {
      gap: 6px;

      .num {
        width: 40%;
        height: 1.3rem;
      }

      .lbl {
        width: 80%;
        height: 0.78rem;
      }
    }
  }

  .layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 380px;
    gap: 24px;
    align-items: start;
  }

  @media (width < 992px) {
    .layout {
      grid-template-columns: 1fr;
    }
  }

  .main-col > .r-card,
  .sidebar-col > .r-card {
    gap: 10px;
  }

  .main-col > .travail-a-faire {
    margin-top: 16px;
  }

  .sidebar-col {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .section-title {
    width: 35%;
    height: 1rem;
    margin-bottom: 4px;
  }

  .line {
    width: 90%;
    height: 0.85rem;
  }

  .line.short {
    width: 55%;
    margin-bottom: 12px;
  }
}
</style>
