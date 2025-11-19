# 🎨 Paleta de Colores Oficial - Coffee UTPedidos

## Colores Principales

### Café & Marrón (Identidad de Marca)
```css
--primary-brown: #c26b1d;        /* Marrón principal - Botones, títulos */
--dark-brown: #8f542c;           /* Marrón oscuro - Hover, énfasis */
--light-brown: #d4823e;          /* Marrón claro - Fondos suaves */
--coffee-dark: #5c3a1e;          /* Café oscuro - Textos importantes */
```

### Verde Institucional (UTP)
```css
--utp-green: #003b2b;            /* Verde UTP - Logo, elementos institucionales */
--utp-green-light: #005544;      /* Verde claro - Hover en verde */
```

### Neutros
```css
--white: #ffffff;                /* Blanco - Fondos principales */
--off-white: #f8f9fa;            /* Blanco roto - Fondos alternativos */
--light-gray: #f1f1f1;           /* Gris claro - Bordes, fondos suaves */
--medium-gray: #e3e3e3;          /* Gris medio - Bordes, separadores */
--text-gray: #4b4b4b;            /* Gris texto - Textos secundarios */
--dark-gray: #333333;            /* Gris oscuro - Textos principales */
```

### Estados (Feedback)
```css
--success: #28a745;              /* Verde éxito - Confirmaciones */
--warning: #ffc107;              /* Amarillo advertencia - Alertas */
--danger: #dc3545;               /* Rojo peligro - Errores */
--info: #17a2b8;                 /* Azul info - Información */
```

### Sombras y Efectos
```css
--shadow-sm: 0 2px 4px rgba(140, 84, 44, 0.1);
--shadow-md: 0 4px 8px rgba(140, 84, 44, 0.15);
--shadow-lg: 0 8px 16px rgba(140, 84, 44, 0.2);
--shadow-hover: 0 6px 12px rgba(194, 107, 29, 0.25);
```

---

## Usos Específicos por Componente

### Navbar
- **Fondo:** `--white` (#ffffff)
- **Logo "Coffee":** `--utp-green` (#003b2b)
- **Logo "UTPedidos":** `--primary-brown` (#c26b1d)
- **Links normales:** `--text-gray` (#4b4b4b)
- **Links hover:** `--primary-brown` (#c26b1d)
- **Botones primarios:** Fondo `--primary-brown`, texto `--white`
- **Botones outline:** Border `--primary-brown`, texto `--primary-brown`

### Botones
- **Primario:** Fondo `--primary-brown`, hover `--dark-brown`
- **Secundario:** Fondo `--utp-green`, hover `--utp-green-light`
- **Outline:** Border `--primary-brown`, hover fondo `--light-brown`
- **Texto:** Color `--white` para fondos oscuros

### Cards de Productos
- **Fondo:** `--white`
- **Borde:** `--medium-gray` o ninguno
- **Título:** `--dark-gray`
- **Precio:** `--primary-brown` (destacado)
- **Descripción:** `--text-gray`
- **Botón:** `--primary-brown`
- **Shadow:** `--shadow-md`, hover `--shadow-hover`

### Filtros/Categorías
- **Normal:** Fondo `--white`, border `--medium-gray`, texto `--text-gray`
- **Hover:** Fondo `--light-gray`, border `--primary-brown`
- **Activo:** Fondo `--primary-brown`, texto `--white`

### Formularios
- **Input normal:** Border `--medium-gray`, fondo `--white`
- **Input focus:** Border `--primary-brown`, shadow `--shadow-sm`
- **Labels:** Color `--dark-gray`
- **Placeholders:** Color `--text-gray`

### Footer
- **Fondo:** `--dark-gray` (#333)
- **Texto:** `--light-gray`
- **Links:** `--primary-brown`
- **Links hover:** `--light-brown`

### Modales
- **Header:** Fondo `--primary-brown`, texto `--white`
- **Body:** Fondo `--white`
- **Border:** `--medium-gray`
- **Overlay:** rgba(0, 0, 0, 0.5)

### Estados de Mensajes
- **Éxito:** Fondo `rgba(40, 167, 69, 0.1)`, border `--success`
- **Error:** Fondo `rgba(220, 53, 69, 0.1)`, border `--danger`
- **Advertencia:** Fondo `rgba(255, 193, 7, 0.1)`, border `--warning`
- **Info:** Fondo `rgba(23, 162, 184, 0.1)`, border `--info`

---

## Tipografía

### Fuentes
```css
--font-primary: 'Outfit', sans-serif;        /* Textos generales */
--font-logo: 'Pacifico', cursive;            /* Logo "Coffee" */
--font-brand: 'Oswald', sans-serif;          /* Logo "UTPedidos" */
```

### Tamaños
```css
--text-xs: 0.75rem;      /* 12px */
--text-sm: 0.875rem;     /* 14px */
--text-base: 1rem;       /* 16px */
--text-lg: 1.125rem;     /* 18px */
--text-xl: 1.25rem;      /* 20px */
--text-2xl: 1.5rem;      /* 24px */
--text-3xl: 1.875rem;    /* 30px */
--text-4xl: 2.25rem;     /* 36px */
```

---

## Espaciado

```css
--spacing-xs: 0.25rem;   /* 4px */
--spacing-sm: 0.5rem;    /* 8px */
--spacing-md: 1rem;      /* 16px */
--spacing-lg: 1.5rem;    /* 24px */
--spacing-xl: 2rem;      /* 32px */
--spacing-2xl: 3rem;     /* 48px */
--spacing-3xl: 4rem;     /* 64px */
```

---

## Border Radius

```css
--radius-sm: 0.25rem;    /* 4px - Inputs pequeños */
--radius-md: 0.5rem;     /* 8px - Botones, cards */
--radius-lg: 1rem;       /* 16px - Modales, cards grandes */
--radius-xl: 1.5rem;     /* 24px - Hero sections */
--radius-pill: 50px;     /* Pills, tags */
--radius-circle: 50%;    /* Avatars, iconos circulares */
```

---

## Ejemplos de Uso

### Botón Primario
```css
.btn-primary {
    background-color: var(--primary-brown);
    color: var(--white);
    border: 2px solid var(--primary-brown);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-sm);
}

.btn-primary:hover {
    background-color: var(--dark-brown);
    border-color: var(--dark-brown);
    box-shadow: var(--shadow-hover);
}
```

### Card de Producto
```css
.product-card {
    background-color: var(--white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-md);
    border: 1px solid var(--light-gray);
}

.product-card:hover {
    box-shadow: var(--shadow-hover);
    border-color: var(--primary-brown);
}

.product-price {
    color: var(--primary-brown);
    font-weight: 600;
}
```

---

## ⚠️ NO USAR

### Colores Prohibidos
❌ **Morados/Púrpuras:** #667eea, #764ba2, etc.
❌ **Degradados coloridos:** Usar solo degradados sutiles de marrón si es necesario
❌ **Colores neón:** Amarillos/verdes brillantes
❌ **Azules vibrantes:** Usar solo el azul info específico

### Efectos Prohibidos
❌ **Degradados multicolor**
❌ **Sombras de colores**
❌ **Animaciones excesivas**
❌ **Bordes con colores distintos a la paleta**

---

## 📐 Variables CSS Completas

```css
:root {
    /* Colores Principales */
    --primary-brown: #c26b1d;
    --dark-brown: #8f542c;
    --light-brown: #d4823e;
    --coffee-dark: #5c3a1e;
    
    /* Verde UTP */
    --utp-green: #003b2b;
    --utp-green-light: #005544;
    
    /* Neutros */
    --white: #ffffff;
    --off-white: #f8f9fa;
    --light-gray: #f1f1f1;
    --medium-gray: #e3e3e3;
    --text-gray: #4b4b4b;
    --dark-gray: #333333;
    
    /* Estados */
    --success: #28a745;
    --warning: #ffc107;
    --danger: #dc3545;
    --info: #17a2b8;
    
    /* Sombras */
    --shadow-sm: 0 2px 4px rgba(140, 84, 44, 0.1);
    --shadow-md: 0 4px 8px rgba(140, 84, 44, 0.15);
    --shadow-lg: 0 8px 16px rgba(140, 84, 44, 0.2);
    --shadow-hover: 0 6px 12px rgba(194, 107, 29, 0.25);
    
    /* Tipografía */
    --font-primary: 'Outfit', sans-serif;
    --font-logo: 'Pacifico', cursive;
    --font-brand: 'Oswald', sans-serif;
    
    /* Tamaños de Texto */
    --text-xs: 0.75rem;
    --text-sm: 0.875rem;
    --text-base: 1rem;
    --text-lg: 1.125rem;
    --text-xl: 1.25rem;
    --text-2xl: 1.5rem;
    --text-3xl: 1.875rem;
    --text-4xl: 2.25rem;
    
    /* Espaciado */
    --spacing-xs: 0.25rem;
    --spacing-sm: 0.5rem;
    --spacing-md: 1rem;
    --spacing-lg: 1.5rem;
    --spacing-xl: 2rem;
    --spacing-2xl: 3rem;
    --spacing-3xl: 4rem;
    
    /* Border Radius */
    --radius-sm: 0.25rem;
    --radius-md: 0.5rem;
    --radius-lg: 1rem;
    --radius-xl: 1.5rem;
    --radius-pill: 50px;
    --radius-circle: 50%;
}
```

---

**Última actualización:** 19 de Noviembre, 2025
**Diseño por:** Coffee UTPedidos Team
