!function ($) {

    "use strict"; // jshint ;_;


    /* COLLAPSE PUBLIC CLASS DEFINITION
  * ================================ */

    var Collapse = function (element, options) {
        this.$element = $(element)//��Ӧ accordion-body
        this.options = $.extend({}, $.fn.collapse.defaults, options)

        if (this.options.parent) {
            this.$parent = $(this.options.parent)
        }

        this.options.toggle && this.toggle()
    }

    Collapse.prototype = {

        constructor: Collapse

        ,
        dimension: function () {
            var hasWidth = this.$element.hasClass('width')
            return hasWidth ? 'width' : 'height'
        }

        ,
        show: function () {
            var dimension
            , scroll
            , actives
            , hasData

            if (this.transitioning) return
            var theIcon = this.$element.parent().find('.accordion-icon');
            theIcon.removeClass('accordion-icon-close');
            theIcon.addClass('accordion-icon-open');
            
            dimension = this.dimension()
            //���û��ָ��width����
            scroll = $.camelCase(['scroll', dimension].join('-'))//���scrollWidth ��scrollHeight
            //�ҵ�����ַ������������չ�������
            actives = this.$parent && this.$parent.find('> .accordion-group > .in')
            //Ȼ���������
//            if (actives && actives.length) {
//                hasData = actives.data('collapse')
//                if (hasData && hasData.transitioning) return
//                actives.collapse('hide')//��������,����ȥ���ǵ�ʵ��
//                hasData || actives.data('collapse', null)
//            }
            //�õ�ǰ���ĸ߶Ȼ���Ϊ��
            this.$element[dimension](0)
            //��ʼ�����봥���¼�
            this.transition('addClass', $.Event('show'), 'shown')

            $.support.transition && this.$element[dimension](this.$element[0][scroll])
        }

        ,
        hide: function () {
            var dimension
            if (this.transitioning) return
            dimension = this.dimension()
            this.reset(this.$element[dimension]())
            this.transition('removeClass', $.Event('hide'), 'hidden')
            this.$element[dimension](0)
            var theIcon = this.$element.parent().find('.accordion-icon');
            theIcon.removeClass('accordion-icon-open');
            theIcon.addClass('accordion-icon-close');
        }

        ,
        reset: function (size) {
            var dimension = this.dimension()

            this.$element
            .removeClass('collapse')
            [dimension](size || 'auto')//��ԭΪԭ���Ĵ�С
            [0].offsetWidth

            this.$element[size !== null ? 'addClass' : 'removeClass']('collapse')

            return this
        }

        ,
        transition: function (method, startEvent, completeEvent) {
            var that = this
            , complete = function () {
                if (startEvent.type == 'show') that.reset()
                that.transitioning = 0
                that.$element.trigger(completeEvent)
            }

            this.$element.trigger(startEvent)

            if (startEvent.isDefaultPrevented()) return

            this.transitioning = 1
            //��ӻ��Ƴ�����in
            this.$element[method]('in')

            $.support.transition && this.$element.hasClass('collapse') ?
            this.$element.one($.support.transition.end, complete) :
            complete()
        }

        ,
        toggle: function () {
            this[this.$element.hasClass('in') ? 'hide' : 'show']()
        }

    }


    /* COLLAPSE PLUGIN DEFINITION
  * ========================== */

    var old = $.fn.collapse

    $.fn.collapse = function (option) {
        return this.each(function () {
            var $this = $(this)
            , data = $this.data('collapse')
            , options = typeof option == 'object' && option
            if (!data) $this.data('collapse', (data = new Collapse(this, options)))
            if (typeof option == 'string') data[option]()
        })
    }

    $.fn.collapse.defaults = {
        toggle: true
    }

    $.fn.collapse.Constructor = Collapse


    /* COLLAPSE NO CONFLICT
  * ==================== */

    $.fn.collapse.noConflict = function () {
        $.fn.collapse = old
        return this
    }


    /* COLLAPSE DATA-API
  * ================= */

    $(document).on('click.collapse.data-api', '[data-toggle=collapse]', function (e) {
        var $this = $(this), href
        , target = $this.attr('data-target') //ȡ����Ҫչ�����۵�������,1ͨ��'data-target'
        || e.preventDefault() //2ͨ��href
        || (href = $this.attr('href')) && href.replace(/.*(?=#[^\s]+$)/, '') //strip for ie7
        , option = $(target).data('collapse') ? 'toggle' : $this.data()
        $this[$(target).hasClass('in') ? 'addClass' : 'removeClass']('collapsed')//?��CSS��û�п���������
        $(target).collapse(option)//��ʼ���ַ��� bootstrap�и��ص�,���ǵ��ʱ�ſ�ʼ��ʼ�����
    })

}(window.jQuery);