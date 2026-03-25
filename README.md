# Unife

**Unife** is a Minecraft integration mod that bridges FE (Forge Energy) power systems with the electrical simulation from [PowerGrid](https://github.com/patryk3211/PowerGrid), allowing generators and machines from any FE-compatible mod to feed real electrical circuits.

---

## Content

### FE Converter
A block that reads FE energy from neighboring machines and converts it into proportional electrical voltage for the PowerGrid network.

- Compatible with **any mod that exposes `IEnergyStorage`** (Mekanism, Thermal Expansion, AE2, and more)
- Supports Immersive Engineering LV, MV, and HV connectors for automatic voltage/throughput scaling *(optional)*
- Dismantlable with the **Create Wrench** — items stack normally in your inventory
- Rotatable with the Wrench (right-click) to point the terminals in the correct direction

| Source | Tier detected | Max Voltage | FE/tick |
|--------|--------------|-------------|---------|
| Any FE mod | Default | 120 V | 256 |
| IE LV connector | LV | 360 V | 256 |
| IE MV connector | MV | 720 V | 1024 |
| IE HV connector | HV | 1000 V | 4096 |

---

## Dependencies

| Mod | Version | Required |
|-----|---------|----------|
| [Create](https://modrinth.com/mod/create) | 0.5.1 (1.20.1) / 6.0 (1.21.1) | ✅ |
| [PowerGrid](https://github.com/patryk3211/PowerGrid) | 0.5.4+ | ✅ |
| [Immersive Engineering](https://modrinth.com/mod/immersiveengineering) | 1.0+ | ⚪ Optional |

> **Without Immersive Engineering** the mod works normally — all FE sources are treated as default tier (120 V / 256 FE/tick).

---

## Supported Versions

| Minecraft | Loader    | Status      |
|-----------|-----------|-------------|
| 1.20.1    | Forge     | ✅ Stable    |
| 1.21.1    | NeoForge  | ✅ Stable    |

---

## How to Use

1. **Craft** the FE Converter (recipe visible in JEI/REI)
2. **Place** the block with its connection face pointing toward an energy block (generator, battery, etc.)
3. The **electrical terminals** are exposed on the top face — connect them to your PowerGrid wiring
4. Energy is pulled automatically each tick and converted into voltage proportional to the buffer fill level

---

## How It Works

The FE Converter pulls FE energy from adjacent blocks each tick and feeds it into a `VoltageSourceCoupling` inside the PowerGrid simulation. The output voltage scales with how full the internal buffer is relative to the detected tier cap. Power lost to internal resistance is deducted from the buffer each tick.

```
FE source → FE Converter buffer → VoltageSourceCoupling → PowerGrid circuit
```

---

## License

This project is licensed under the [MIT License](LICENSE).