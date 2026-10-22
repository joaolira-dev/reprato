import { useEffect, useMemo, useState } from 'react'
import {
  ArrowDownRight, ArrowRight, Check, Clock3, Heart, Leaf, MapPin,
  Menu, Minus, Plus, Search, ShoppingBag, Sparkles, X,
} from 'lucide-react'

type Category = 'Todos' | 'Padaria' | 'Refeições' | 'Doces' | 'Hortifruti'
type Offer = {
  id: number
  title: string
  merchant: string
  district: string
  category: Exclude<Category, 'Todos'>
  price: number
  originalPrice: number
  units: number
  pickup: string
  image: string
  accent: string
  description: string
}

const demoOffers: Offer[] = [
  {
    id: 1, title: 'Cesta da fornada', merchant: 'Pão de Lume', district: 'Bessa',
    category: 'Padaria', price: 19.9, originalPrice: 39.9, units: 4,
    pickup: '17h30 – 19h', image: '/images/bread.jpg', accent: '#d8b885',
    description: 'Uma seleção surpresa de pães feitos hoje. O conteúdo varia conforme a fornada do dia.',
  },
  {
    id: 2, title: 'Almoço do quintal', merchant: 'Quintal Cozinha', district: 'Manaíra',
    category: 'Refeições', price: 24.9, originalPrice: 48.9, units: 3,
    pickup: '15h – 17h', image: '/images/bowl.jpg', accent: '#b8c9a8',
    description: 'Uma refeição preparada no dia, com vegetais e acompanhamentos disponíveis na cozinha.',
  },
  {
    id: 3, title: 'Surpresa da vitrine', merchant: 'Dona Miga', district: 'Tambaú',
    category: 'Doces', price: 16.9, originalPrice: 34.9, units: 2,
    pickup: '18h – 19h30', image: '/images/pastry.jpg', accent: '#d4aca0',
    description: 'Doces e massas folhadas da vitrine de hoje, escolhidos pela equipe da casa.',
  },
  {
    id: 4, title: 'Pães para partilhar', merchant: 'Casa do Trigo', district: 'Cabo Branco',
    category: 'Padaria', price: 22.9, originalPrice: 45.9, units: 5,
    pickup: '18h – 20h', image: '/images/bakery.jpg', accent: '#d1ad79',
    description: 'Pães e itens de padaria feitos no dia para levar para casa e dividir.',
  },
]

const categories: Category[] = ['Todos', 'Padaria', 'Refeições', 'Doces', 'Hortifruti']
const money = (value: number) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)

function Logo() {
  return <a className="brand" href="#inicio" aria-label="Reprato, ir para o início">
    <span className="brand-mark" aria-hidden="true"><Leaf size={20} strokeWidth={2.2} /></span>
    <span>re<span className="brand-italic">prato</span><span className="brand-period">.</span></span>
  </a>
}

function App() {
  const [category, setCategory] = useState<Category>('Todos')
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState<Offer | null>(null)
  const [quantity, setQuantity] = useState(1)
  const [confirmation, setConfirmation] = useState<string | null>(null)
  const [favorites, setFavorites] = useState<number[]>([])
  const [showFavorites, setShowFavorites] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {
    document.body.style.overflow = selected ? 'hidden' : ''
    return () => { document.body.style.overflow = '' }
  }, [selected])

  const visibleOffers = useMemo(() => demoOffers.filter((offer) => {
    const categoryMatches = category === 'Todos' || offer.category === category
    const searchMatches = `${offer.title} ${offer.merchant} ${offer.district}`.toLocaleLowerCase('pt-BR').includes(search.toLocaleLowerCase('pt-BR'))
    return categoryMatches && searchMatches && (!showFavorites || favorites.includes(offer.id))
  }), [category, search, showFavorites, favorites])

  function openOffer(offer: Offer) {
    setSelected(offer)
    setQuantity(1)
    setConfirmation(null)
  }

  function reserve() {
    if (!selected) return
    setConfirmation(`Reserva de demonstração para ${quantity} ${quantity === 1 ? 'kit' : 'kits'} de ${selected.merchant}.`)
  }

  function toggleFavorite(id: number) {
    setFavorites((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id])
  }

  return <>
    <div className="announcement"><span>Comida boa tem futuro.</span><span className="announcement-divider" /> <span>Descubra o que ainda dá tempo de aproveitar hoje.</span><ArrowRight size={14} /></div>
    <header className="site-header" id="inicio">
      <div className="container nav-row">
        <Logo />
        <nav className={menuOpen ? 'main-nav open' : 'main-nav'} aria-label="Navegação principal">
          <a href="#ofertas" onClick={() => setMenuOpen(false)}>Explorar ofertas</a>
          <a href="#como-funciona" onClick={() => setMenuOpen(false)}>Como funciona</a>
          <a href="#para-negocios" onClick={() => setMenuOpen(false)}>Para negócios</a>
        </nav>
        <div className="nav-actions">
          <span className="location"><MapPin size={15} /> João Pessoa, PB</span>
          <button className="bag-button" type="button" onClick={() => { setShowFavorites((value) => !value); document.getElementById('ofertas')?.scrollIntoView({ behavior: 'smooth' }) }} aria-label={showFavorites ? 'Mostrar todas as ofertas' : 'Mostrar favoritos'} title="Favoritos"><Heart size={19} fill={showFavorites ? 'currentColor' : 'none'} /><span>{favorites.length}</span></button>
          <button className="mobile-menu" type="button" aria-label="Abrir menu" aria-expanded={menuOpen} onClick={() => setMenuOpen((value) => !value)}>{menuOpen ? <X size={22} /> : <Menu size={22} />}</button>
        </div>
      </div>
    </header>

    <main>
      <section className="hero">
        <div className="container hero-grid">
          <div className="hero-copy">
            <div className="eyebrow"><span className="eyebrow-line" /> A BOA ESCOLHA DE HOJE</div>
            <h1>Comida boa merece <em>outro destino.</em></h1>
            <p>Encontre kits fresquinhos de lugares perto de você. Aproveite mais, desperdice menos e dê uma nova chance ao que foi feito hoje.</p>
            <div className="hero-actions"><a className="button button-dark" href="#ofertas">Explorar ofertas <ArrowRight size={19} /></a><a className="text-link" href="#como-funciona">Entenda a ideia <ArrowDownRight size={18} /></a></div>
            <div className="hero-note"><span className="note-asterisk">✳</span><span>Para retirar pessoalmente<br /><strong>no seu bairro.</strong></span></div>
          </div>
          <div className="hero-visual">
            <div className="hero-photo"><img src="/images/bread.jpg" alt="Pães artesanais sobre uma mesa" /></div>
            <div className="hero-stamp" aria-label="Bom para hoje, melhor para amanhã"><span>BOM PARA HOJE</span><strong>✳</strong><span>MELHOR PARA AMANHÃ</span></div>
            <div className="hero-caption"><span>01 / 03</span><span>FEITO HOJE, PARA HOJE.</span></div>
          </div>
        </div>
      </section>

      <div className="ticker" aria-hidden="true"><div className="ticker-content"><span>MENOS DESPERDÍCIO</span><b>✳</b><span>MAIS SABOR</span><b>✳</b><span>PERTO DE VOCÊ</span><b>✳</b><span>MENOS DESPERDÍCIO</span><b>✳</b><span>MAIS SABOR</span><b>✳</b><span>PERTO DE VOCÊ</span><b>✳</b></div></div>

      <section className="offers-section" id="ofertas">
        <div className="container">
          <div className="section-head"><div><div className="eyebrow eyebrow-green"><span className="eyebrow-line" /> O QUE TEM PRA HOJE</div><h2>Oportunidades<br /><em>que dão gosto.</em></h2></div><p>De fornadas a refeições completas: boas descobertas, com preço gentil e retirada perto de você.</p></div>
          <div className="filter-bar"><div className="category-list" role="group" aria-label="Filtrar por categoria">{categories.map((item) => <button key={item} type="button" className={category === item ? 'category active' : 'category'} onClick={() => { setCategory(item); setShowFavorites(false) }}>{item}</button>)}</div><label className="search-field"><Search size={18} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Busque um lugar ou kit" aria-label="Buscar ofertas" /></label></div>
          {showFavorites && <div className="filter-notice"><Heart size={15} fill="currentColor" /> Mostrando seus favoritos <button type="button" onClick={() => setShowFavorites(false)}>ver todos</button></div>}
          <div className="offers-grid">{visibleOffers.length ? visibleOffers.map((offer) => <article className="offer-card" key={offer.id}>
            <div className="offer-image" style={{ backgroundColor: offer.accent }}><img src={offer.image} alt={`Kit ilustrativo: ${offer.title}`} /><span className="offer-badge">SALVE ESTE KIT</span><button type="button" className={favorites.includes(offer.id) ? 'favorite active' : 'favorite'} aria-label={favorites.includes(offer.id) ? `Remover ${offer.title} dos favoritos` : `Favoritar ${offer.title}`} onClick={() => toggleFavorite(offer.id)}><Heart size={18} fill={favorites.includes(offer.id) ? 'currentColor' : 'none'} /></button></div>
            <div className="offer-body"><div className="offer-meta"><span>{offer.category}</span><span><MapPin size={13} /> {offer.district}</span></div><h3>{offer.title}</h3><p className="merchant-name">por {offer.merchant}</p><div className="offer-pickup"><Clock3 size={15} /> Retire hoje, {offer.pickup}</div><div className="offer-bottom"><div className="price"><span>de <s>{money(offer.originalPrice)}</s> por</span><strong>{money(offer.price)}</strong></div><button type="button" className="card-cta" onClick={() => openOffer(offer)} aria-label={`Ver kit ${offer.title}`}><ArrowRight size={21} /></button></div></div>
          </article>) : <div className="empty-state"><Sparkles size={26} /><h3>Nada por aqui ainda.</h3><p>Tente outra categoria ou busca. As ofertas de demonstração voltam quando você limpa os filtros.</p><button type="button" onClick={() => { setSearch(''); setCategory('Todos'); setShowFavorites(false) }}>Limpar filtros <ArrowRight size={16} /></button></div>}</div>
          <div className="demo-disclaimer">Ofertas e estabelecimentos fictícios para demonstração do projeto. Nenhuma compra é realizada nesta versão.</div>
        </div>
      </section>

      <section className="how-section" id="como-funciona"><div className="container how-grid"><div className="how-intro"><div className="eyebrow eyebrow-light"><span className="eyebrow-line" /> SIMPLES ASSIM</div><h2>Uma boa ideia.<br /><em>Três passos.</em></h2><p>O que não foi vendido hoje ainda pode fazer o dia de alguém.</p><a href="#ofertas" className="text-link light-link">Encontrar meu kit <ArrowRight size={18} /></a></div><div className="steps"><div className="step"><span>01</span><div><h3>Escolha</h3><p>Veja as ofertas do dia feitas por negócios da sua região.</p></div><ArrowDownRight size={23} /></div><div className="step"><span>02</span><div><h3>Reserve</h3><p>Garanta seu kit enquanto ele ainda está disponível.</p></div><ArrowDownRight size={23} /></div><div className="step"><span>03</span><div><h3>Retire</h3><p>Passe no estabelecimento durante o horário combinado.</p></div><ArrowDownRight size={23} /></div></div></div></section>

      <section className="business-section" id="para-negocios"><div className="container business-grid"><div className="business-art"><img src="/images/pastry.jpg" alt="Croissants frescos preparados na padaria" /><div className="business-sticker">BOM PARA TODO MUNDO <span>✳</span></div></div><div className="business-copy"><div className="eyebrow eyebrow-green"><span className="eyebrow-line" /> TEM UM NEGÓCIO?</div><h2>Seu trabalho merece chegar <em>à mesa.</em></h2><p>Uma vitrine simples para transformar a produção do dia em uma nova oportunidade. Este é um produto de estudo; a área do lojista será construída nas próximas etapas.</p><span className="business-status">ÁREA DO LOJISTA · EM CONSTRUÇÃO</span></div></div></section>
    </main>

    <footer><div className="container footer-main"><div><Logo /><p>Uma segunda chance para comida boa.</p></div><div className="footer-links"><a href="#ofertas">Ofertas</a><a href="#como-funciona">Como funciona</a><a href="#para-negocios">Para negócios</a></div></div><div className="container footer-bottom"><span>© 2026 REPRATO · PROJETO DE PORTFÓLIO</span><span>FEITO PARA ESTUDAR PRODUTO E ENGENHARIA <Leaf size={14} /></span></div></footer>

    {selected && <div className="modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setSelected(null) }}><div className="offer-modal" role="dialog" aria-modal="true" aria-labelledby="modal-title"><button className="modal-close" type="button" aria-label="Fechar" onClick={() => setSelected(null)}><X size={21} /></button><div className="modal-image"><img src={selected.image} alt="Fotografia ilustrativa do kit" /></div><div className="modal-content">{confirmation ? <div className="confirmation"><div className="confirmation-icon"><Check size={28} /></div><span className="modal-kicker">VERSÃO DE DEMONSTRAÇÃO</span><h2>Boa escolha!</h2><p>{confirmation}</p><p>O fluxo real de reserva será conectado aos serviços de pedidos e estoque. Nenhum pedido foi enviado a um estabelecimento.</p><button className="button button-dark" type="button" onClick={() => setSelected(null)}>Voltar às ofertas <ArrowRight size={18} /></button></div> : <><span className="modal-kicker">{selected.category} · {selected.district}</span><h2 id="modal-title">{selected.title}</h2><p className="modal-merchant">por {selected.merchant}</p><p className="modal-description">{selected.description}</p><div className="modal-info"><Clock3 size={18} /><div><strong>Retirada hoje</strong><span>{selected.pickup} · {selected.district}, João Pessoa</span></div></div><div className="modal-info"><ShoppingBag size={18} /><div><strong>Disponibilidade demonstrativa</strong><span>{selected.units} {selected.units === 1 ? 'kit' : 'kits'} nesta oferta</span></div></div><div className="quantity-row"><span>Quantidade</span><div className="quantity-control"><button type="button" aria-label="Diminuir quantidade" disabled={quantity === 1} onClick={() => setQuantity((value) => value - 1)}><Minus size={16} /></button><span>{quantity}</span><button type="button" aria-label="Aumentar quantidade" disabled={quantity === selected.units} onClick={() => setQuantity((value) => value + 1)}><Plus size={16} /></button></div></div><div className="modal-total"><span>Total demonstrativo</span><strong>{money(selected.price * quantity)}</strong></div><button className="button button-dark reserve-button" type="button" onClick={reserve}>Simular reserva <ArrowRight size={18} /></button><small>Simulação local: nenhuma cobrança ou reserva real.</small></>}</div></div></div>}
  </>
}

export default App
